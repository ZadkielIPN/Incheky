package org.handel.incheki.service.impl;

import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.response.GastoResponse;
import org.handel.incheki.dtos.response.ReporteAbastecimientoResponse;
import org.handel.incheki.dtos.response.ReporteContableResponse;
import org.handel.incheki.dtos.response.ReporteVentaResponse;
import org.handel.incheki.model.Abastecimiento;
import org.handel.incheki.model.DetalleVenta;
import org.handel.incheki.model.Gasto;
import org.handel.incheki.model.Venta;
import org.handel.incheki.repository.AbastecimientoRepository;
import org.handel.incheki.repository.GastoRepository;
import org.handel.incheki.repository.VentaRepository;
import org.handel.incheki.service.ReporteContableService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteContableServiceImpl implements ReporteContableService {
    private final VentaRepository ventaRepository;
    private final AbastecimientoRepository abastecimientoRepository;
    private final GastoRepository gastoRepository;

    @Override
    @Transactional(readOnly = true)
    public ReporteContableResponse consultar(int anio, int mes) {
        final YearMonth periodo;
        try {
            periodo = YearMonth.of(anio, mes);
        } catch (DateTimeException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El mes y año del reporte no son válidos.");
        }

        LocalDateTime inicio = periodo.atDay(1).atStartOfDay();
        LocalDateTime fin = periodo.plusMonths(1).atDay(1).atStartOfDay();
        List<Venta> ventas = ventaRepository.findByFechaGreaterThanEqualAndFechaLessThanOrderByFechaAscIdAsc(inicio, fin);
        List<Abastecimiento> abastecimientos = abastecimientoRepository
                .findByFechaGreaterThanEqualAndFechaLessThanOrderByFechaAscIdAsc(inicio, fin);
        List<Gasto> gastos = gastoRepository.findByFechaGreaterThanEqualAndFechaLessThanOrderByFechaAscIdAsc(inicio, fin);

        List<ReporteVentaResponse> filasVenta = ventas.stream().map(this::mapVenta).toList();
        List<ReporteAbastecimientoResponse> filasAbastecimiento = abastecimientos.stream()
                .map(item -> new ReporteAbastecimientoResponse(item.getId(), item.getFecha(),
                        item.getProveedor().getNombre(), dinero(item.getTotal())))
                .toList();
        List<GastoResponse> filasGasto = gastos.stream().map(this::mapGasto).toList();

        BigDecimal subtotalVentas = sumar(filasVenta.stream().map(ReporteVentaResponse::getSubtotal).toList());
        BigDecimal descuentosVentas = sumar(filasVenta.stream().map(ReporteVentaResponse::getDescuentos).toList());
        BigDecimal ingresos = sumar(ventas.stream().map(Venta::getTotal).toList());
        BigDecimal costoMercancia = sumar(filasVenta.stream().map(ReporteVentaResponse::getCostoMercancia).toList());
        BigDecimal utilidadBruta = ingresos.subtract(costoMercancia).setScale(2, RoundingMode.HALF_UP);
        BigDecimal gastosOperativos = sumar(gastos.stream().map(Gasto::getMonto).toList());
        BigDecimal comprasInventario = sumar(abastecimientos.stream().map(Abastecimiento::getTotal).toList());

        return new ReporteContableResponse(anio, mes, subtotalVentas, descuentosVentas, ingresos,
                costoMercancia, utilidadBruta, gastosOperativos,
                utilidadBruta.subtract(gastosOperativos).setScale(2, RoundingMode.HALF_UP),
                comprasInventario, filasVenta, filasAbastecimiento, filasGasto);
    }

    private ReporteVentaResponse mapVenta(Venta venta) {
        BigDecimal costo = BigDecimal.ZERO;
        BigDecimal subtotal = BigDecimal.ZERO;
        for (DetalleVenta detalle : venta.getDetalles()) {
            if (detalle.getCostoUnitario() == null || detalle.getSubtotal() == null) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "No hay costo histórico completo para calcular la utilidad del mes. Verifica el historial de abastecimientos.");
            }
            costo = costo.add(detalle.getCostoUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad()))
                    .setScale(2, RoundingMode.HALF_UP));
            subtotal = subtotal.add(detalle.getSubtotal());
        }
        costo = dinero(costo);
        subtotal = dinero(subtotal);
        BigDecimal total = dinero(venta.getTotal());
        BigDecimal descuentos = dinero(subtotal.subtract(total));
        String cliente = venta.getCliente() == null ? "" : venta.getCliente().getNombre() + " " + venta.getCliente().getApellido();
        return new ReporteVentaResponse(venta.getId(), venta.getFecha(), cliente.trim(), venta.getMetodoPago(),
                subtotal, descuentos, total, costo, dinero(total.subtract(costo)));
    }

    private GastoResponse mapGasto(Gasto gasto) {
        return new GastoResponse(gasto.getId(), gasto.getFecha(), gasto.getCategoria(),
                gasto.getDescripcion(), dinero(gasto.getMonto()));
    }

    private BigDecimal sumar(List<BigDecimal> valores) {
        return dinero(valores.stream().reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    private BigDecimal dinero(BigDecimal importe) {
        return importe.setScale(2, RoundingMode.HALF_UP);
    }
}
