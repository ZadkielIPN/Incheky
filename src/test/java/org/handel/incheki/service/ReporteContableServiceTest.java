package org.handel.incheki.service;

import org.handel.incheki.dtos.response.ReporteContableResponse;
import org.handel.incheki.enums.MetodoPago;
import org.handel.incheki.model.DetalleVenta;
import org.handel.incheki.model.Gasto;
import org.handel.incheki.model.Venta;
import org.handel.incheki.repository.AbastecimientoRepository;
import org.handel.incheki.repository.GastoRepository;
import org.handel.incheki.repository.VentaRepository;
import org.handel.incheki.service.impl.ReporteContableServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReporteContableServiceTest {
    @Mock
    private VentaRepository ventaRepository;
    @Mock
    private AbastecimientoRepository abastecimientoRepository;
    @Mock
    private GastoRepository gastoRepository;

    private ReporteContableService service;

    @BeforeEach
    void setUp() {
        service = new ReporteContableServiceImpl(ventaRepository, abastecimientoRepository, gastoRepository);
    }

    @Test
    void calculatesMonthlyNetProfitAndUsesHalfOpenMonthRange() {
        LocalDateTime inicio = LocalDateTime.of(2026, 11, 1, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 12, 1, 0, 0);
        Venta venta = new Venta();
        venta.setId(17L);
        venta.setFecha(LocalDateTime.of(2026, 11, 20, 12, 30));
        venta.setTotal(new BigDecimal("100.00"));
        venta.setMetodoPago(MetodoPago.EFECTIVO);
        DetalleVenta detalle = new DetalleVenta();
        detalle.setCantidad(2);
        detalle.setCostoUnitario(new BigDecimal("30.000000"));
        detalle.setSubtotal(new BigDecimal("100.00"));
        venta.setDetalles(List.of(detalle));
        Gasto gasto = Gasto.builder().id(4L).fecha(LocalDateTime.of(2026, 11, 10, 9, 0))
                .categoria("Servicios").descripcion("Internet").monto(new BigDecimal("10.00")).build();

        when(ventaRepository.findByFechaGreaterThanEqualAndFechaLessThanOrderByFechaAscIdAsc(inicio, fin))
                .thenReturn(List.of(venta));
        when(abastecimientoRepository.findByFechaGreaterThanEqualAndFechaLessThanOrderByFechaAscIdAsc(inicio, fin))
                .thenReturn(List.of());
        when(gastoRepository.findByFechaGreaterThanEqualAndFechaLessThanOrderByFechaAscIdAsc(inicio, fin))
                .thenReturn(List.of(gasto));

        ReporteContableResponse report = service.consultar(2026, 11);

        assertEquals(new BigDecimal("100.00"), report.getIngresosVentas());
        assertEquals(new BigDecimal("60.00"), report.getCostoMercanciaVendida());
        assertEquals(new BigDecimal("40.00"), report.getUtilidadBruta());
        assertEquals(new BigDecimal("10.00"), report.getGastosOperativos());
        assertEquals(new BigDecimal("30.00"), report.getUtilidadNeta());
        verify(ventaRepository).findByFechaGreaterThanEqualAndFechaLessThanOrderByFechaAscIdAsc(inicio, fin);
    }

    @Test
    void rejectsSalesWithoutHistoricalCostInsteadOfReportingFalseProfit() {
        LocalDateTime inicio = LocalDateTime.of(2026, 11, 1, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 12, 1, 0, 0);
        Venta venta = new Venta();
        venta.setFecha(LocalDateTime.of(2026, 11, 5, 10, 0));
        venta.setTotal(new BigDecimal("20.00"));
        DetalleVenta detalle = new DetalleVenta();
        detalle.setCantidad(1);
        detalle.setSubtotal(new BigDecimal("20.00"));
        venta.setDetalles(List.of(detalle));
        when(ventaRepository.findByFechaGreaterThanEqualAndFechaLessThanOrderByFechaAscIdAsc(inicio, fin))
                .thenReturn(List.of(venta));
        when(abastecimientoRepository.findByFechaGreaterThanEqualAndFechaLessThanOrderByFechaAscIdAsc(inicio, fin))
                .thenReturn(List.of());
        when(gastoRepository.findByFechaGreaterThanEqualAndFechaLessThanOrderByFechaAscIdAsc(inicio, fin))
                .thenReturn(List.of());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.consultar(2026, 11));
        assertEquals(422, exception.getStatusCode().value());
    }

    @Test
    void rejectsInvalidMonth() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.consultar(2026, 13));
        assertEquals(400, exception.getStatusCode().value());
    }
}
