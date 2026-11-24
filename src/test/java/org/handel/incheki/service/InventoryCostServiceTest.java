package org.handel.incheki.service;

import org.handel.incheki.model.Abastecimiento;
import org.handel.incheki.model.DetalleAbastecimiento;
import org.handel.incheki.model.DetalleVenta;
import org.handel.incheki.model.Producto;
import org.handel.incheki.model.Venta;
import org.handel.incheki.enums.UnidadMedida;
import org.handel.incheki.repository.AbastecimientoRepository;
import org.handel.incheki.repository.DetalleVentaRepository;
import org.handel.incheki.repository.ProductoRepository;
import org.handel.incheki.repository.VentaRepository;
import org.handel.incheki.service.impl.InventoryCostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryCostServiceTest {
    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private AbastecimientoRepository abastecimientoRepository;
    @Mock
    private VentaRepository ventaRepository;
    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    private InventoryCostServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new InventoryCostServiceImpl(productoRepository, abastecimientoRepository, ventaRepository,
                detalleVentaRepository);
    }
    @Test
    void calculatesMovingWeightedAverage() {
        BigDecimal average = InventoryCostServiceImpl.promedioPonderado(
                4, new BigDecimal("10.00"), 2, new BigDecimal("15.00"));

        assertEquals(new BigDecimal("11.666667"), average);
    }

    @Test
    void rebuildsHistoricalSaleCostsAndCurrentAverageFromPurchaseHistory() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setStock(2);

        DetalleAbastecimiento primeraCompraDetalle = new DetalleAbastecimiento();
        primeraCompraDetalle.setId(1L);
        primeraCompraDetalle.setProducto(producto);
        primeraCompraDetalle.setCantidad(2);
        primeraCompraDetalle.setCostoUnitario(new BigDecimal("10.00"));
        Abastecimiento primeraCompra = abastecimiento(1L, LocalDateTime.of(2026, 11, 1, 10, 0), primeraCompraDetalle);

        DetalleVenta primeraVentaDetalle = detalleVenta(1L, producto, 1);
        Venta primeraVenta = venta(1L, LocalDateTime.of(2026, 11, 2, 10, 0), primeraVentaDetalle);

        DetalleAbastecimiento segundaCompraDetalle = new DetalleAbastecimiento();
        segundaCompraDetalle.setId(2L);
        segundaCompraDetalle.setProducto(producto);
        segundaCompraDetalle.setCantidad(2);
        segundaCompraDetalle.setCostoUnitario(new BigDecimal("20.00"));
        Abastecimiento segundaCompra = abastecimiento(2L, LocalDateTime.of(2026, 11, 3, 10, 0), segundaCompraDetalle);

        DetalleVenta segundaVentaDetalle = detalleVenta(2L, producto, 1);
        Venta segundaVenta = venta(2L, LocalDateTime.of(2026, 11, 4, 10, 0), segundaVentaDetalle);

        when(productoRepository.findAll()).thenReturn(List.of(producto));
        when(abastecimientoRepository.findAllByOrderByFechaAscIdAsc()).thenReturn(List.of(primeraCompra, segundaCompra));
        when(ventaRepository.findAllByOrderByFechaAscIdAsc()).thenReturn(List.of(primeraVenta, segundaVenta));

        service.rebuildCostsFromHistory();

        assertEquals(new BigDecimal("10.000000"), primeraVentaDetalle.getCostoUnitario());
        assertEquals(new BigDecimal("16.666667"), segundaVentaDetalle.getCostoUnitario());
        assertEquals(new BigDecimal("16.666667"), producto.getCostoPromedio());
    }

    private Abastecimiento abastecimiento(Long id, LocalDateTime fecha, DetalleAbastecimiento detalle) {
        Abastecimiento abastecimiento = new Abastecimiento();
        abastecimiento.setId(id);
        abastecimiento.setFecha(fecha);
        abastecimiento.setDetalles(List.of(detalle));
        return abastecimiento;
    }

    private DetalleVenta detalleVenta(Long id, Producto producto, int cantidad) {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setId(id);
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);
        return detalle;
    }

    private Venta venta(Long id, LocalDateTime fecha, DetalleVenta detalle) {
        Venta venta = new Venta();
        venta.setId(id);
        venta.setFecha(fecha);
        venta.setDetalles(List.of(detalle));
        return venta;
    }

    @Test
    void usesNewPurchaseCostWhenNoUnitsRemain() {
        BigDecimal average = InventoryCostServiceImpl.promedioPonderado(
                0, BigDecimal.ZERO, 3, new BigDecimal("8.25"));

        assertEquals(new BigDecimal("8.250000"), average);
    }

    @Test
    void estimatesMissingCostsUsingMedianRatioForSameUnitAndPreservesExistingCosts() {
        Producto referenciaBaja = producto(UnidadMedida.PIEZA, "100.00", "40.00");
        Producto referenciaAlta = producto(UnidadMedida.PIEZA, "100.00", "60.00");
        Producto sinCosto = producto(UnidadMedida.PIEZA, "100.00", null);
        DetalleVenta detalleSinCosto = detalleVentaConPrecio(sinCosto, "200.00");
        DetalleVenta detalleConCosto = detalleVentaConPrecio(referenciaBaja, "50.00");
        detalleConCosto.setCostoUnitario(new BigDecimal("20.000000"));

        when(productoRepository.findAll()).thenReturn(List.of(referenciaBaja, referenciaAlta, sinCosto));
        when(detalleVentaRepository.findAll()).thenReturn(List.of(detalleSinCosto, detalleConCosto));

        service.estimateMissingCosts();

        assertEquals(new BigDecimal("50.000000"), sinCosto.getCostoPromedio());
        assertEquals(new BigDecimal("100.000000"), detalleSinCosto.getCostoUnitario());
        assertEquals(new BigDecimal("40.00"), referenciaBaja.getCostoPromedio());
        assertEquals(new BigDecimal("20.000000"), detalleConCosto.getCostoUnitario());
    }

    @Test
    void fallsBackToGlobalRatioWhenThereIsNoReferenceWithTheSameUnit() {
        Producto referencia = producto(UnidadMedida.PIEZA, "100.00", "25.00");
        Producto sinCosto = producto(UnidadMedida.KILOGRAMO, "80.00", null);

        when(productoRepository.findAll()).thenReturn(List.of(referencia, sinCosto));
        when(detalleVentaRepository.findAll()).thenReturn(List.of());

        service.estimateMissingCosts();

        assertEquals(new BigDecimal("20.000000"), sinCosto.getCostoPromedio());
    }

    @Test
    void leavesCostsNullWhenThereAreNoReferencePrices() {
        Producto sinPrecio = producto(UnidadMedida.PIEZA, "0.00", null);
        DetalleVenta sinCosto = detalleVentaConPrecio(sinPrecio, "0.00");

        when(productoRepository.findAll()).thenReturn(List.of(sinPrecio));
        when(detalleVentaRepository.findAll()).thenReturn(List.of(sinCosto));

        service.estimateMissingCosts();

        assertNull(sinPrecio.getCostoPromedio());
        assertNull(sinCosto.getCostoUnitario());
    }

    @Test
    void estimatesRemainingCostsAfterHistoricalRebuildByDefault() {
        Producto referencia = producto(UnidadMedida.PIEZA, "100.00", null);
        referencia.setId(1L);
        referencia.setStock(2);
        Producto sinCosto = producto(UnidadMedida.PIEZA, "50.00", null);
        sinCosto.setId(2L);
        sinCosto.setStock(3);
        DetalleAbastecimiento detalleCompra = new DetalleAbastecimiento();
        detalleCompra.setId(1L);
        detalleCompra.setProducto(referencia);
        detalleCompra.setCantidad(2);
        detalleCompra.setCostoUnitario(new BigDecimal("40.00"));
        Abastecimiento compra = abastecimiento(1L, LocalDateTime.of(2026, 11, 1, 10, 0), detalleCompra);

        when(productoRepository.findAll()).thenReturn(List.of(referencia, sinCosto));
        when(abastecimientoRepository.findAllByOrderByFechaAscIdAsc()).thenReturn(List.of(compra));
        when(ventaRepository.findAllByOrderByFechaAscIdAsc()).thenReturn(List.of());
        when(detalleVentaRepository.findAll()).thenReturn(List.of());

        service.rebuildCostsFromHistory();

        assertEquals(new BigDecimal("40.000000"), referencia.getCostoPromedio());
        assertEquals(new BigDecimal("20.000000"), sinCosto.getCostoPromedio());
    }

    @Test
    void canDisableTemporaryEstimatesWithoutDisablingHistoricalRebuild() {
        ReflectionTestUtils.setField(service, "estimateMissingCostsOnRebuild", false);
        Producto referencia = producto(UnidadMedida.PIEZA, "100.00", null);
        referencia.setId(1L);
        referencia.setStock(2);
        Producto sinCosto = producto(UnidadMedida.PIEZA, "50.00", null);
        sinCosto.setId(2L);
        sinCosto.setStock(3);
        DetalleAbastecimiento detalleCompra = new DetalleAbastecimiento();
        detalleCompra.setId(1L);
        detalleCompra.setProducto(referencia);
        detalleCompra.setCantidad(2);
        detalleCompra.setCostoUnitario(new BigDecimal("40.00"));
        Abastecimiento compra = abastecimiento(1L, LocalDateTime.of(2026, 11, 1, 10, 0), detalleCompra);

        when(productoRepository.findAll()).thenReturn(List.of(referencia, sinCosto));
        when(abastecimientoRepository.findAllByOrderByFechaAscIdAsc()).thenReturn(List.of(compra));
        when(ventaRepository.findAllByOrderByFechaAscIdAsc()).thenReturn(List.of());

        service.rebuildCostsFromHistory();

        assertEquals(new BigDecimal("40.000000"), referencia.getCostoPromedio());
        assertNull(sinCosto.getCostoPromedio());
    }

    @Test
    void usesPreviousKnownCostsAsReferenceWhenHistoryStillCannotBeReconciled() {
        Producto referenciaAnterior = producto(UnidadMedida.PIEZA, "100.00", "40.00");
        referenciaAnterior.setId(1L);
        referenciaAnterior.setStock(2);
        Producto sinCosto = producto(UnidadMedida.PIEZA, "50.00", null);
        sinCosto.setId(2L);
        sinCosto.setStock(1);

        when(productoRepository.findAll()).thenReturn(List.of(referenciaAnterior, sinCosto));
        when(abastecimientoRepository.findAllByOrderByFechaAscIdAsc()).thenReturn(List.of());
        when(ventaRepository.findAllByOrderByFechaAscIdAsc()).thenReturn(List.of());
        when(detalleVentaRepository.findAll()).thenReturn(List.of());

        service.rebuildCostsFromHistory();

        assertEquals(new BigDecimal("40.00"), referenciaAnterior.getCostoPromedio());
        assertEquals(new BigDecimal("20.000000"), sinCosto.getCostoPromedio());
    }

    @Test
    void preservesExistingZeroCostWhenStockDoesNotMatchHistoricalMovements() {
        Producto producto = producto(UnidadMedida.PIEZA, "100.00", "0.000000");
        producto.setId(1L);
        producto.setStock(3);

        when(productoRepository.findAll()).thenReturn(List.of(producto));
        when(abastecimientoRepository.findAllByOrderByFechaAscIdAsc()).thenReturn(List.of());
        when(ventaRepository.findAllByOrderByFechaAscIdAsc()).thenReturn(List.of());
        when(detalleVentaRepository.findAll()).thenReturn(List.of());

        service.rebuildCostsFromHistory();

        assertEquals(new BigDecimal("0.000000"), producto.getCostoPromedio());
    }

    @Test
    void defaultsProductCostToZeroForBuilderAndNoArgsConstructor() {
        BigDecimal expected = new BigDecimal("0.000000");

        assertEquals(expected, Producto.builder().build().getCostoPromedio());
        assertEquals(expected, new Producto().getCostoPromedio());
    }

    private Producto producto(UnidadMedida unidad, String precio, String costo) {
        Producto producto = new Producto();
        producto.setUnidadMedida(unidad);
        producto.setPrecio(new BigDecimal(precio));
        producto.setCostoPromedio(costo == null ? null : new BigDecimal(costo));
        return producto;
    }

    private DetalleVenta detalleVentaConPrecio(Producto producto, String precio) {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setProducto(producto);
        detalle.setPrecioUnitario(new BigDecimal(precio));
        return detalle;
    }
}
