package org.handel.incheki.service.impl;

import lombok.RequiredArgsConstructor;
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
import org.handel.incheki.service.InventoryCostService;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryCostServiceImpl implements InventoryCostService {
    private static final Logger log = LoggerFactory.getLogger(InventoryCostServiceImpl.class);
    private static final BigDecimal ZERO_COST = BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);
    private static final BigDecimal ZERO_PRICE = BigDecimal.ZERO;
    private static final int COST_SCALE = 6;
    private static final int RATIO_SCALE = 12;

    private final ProductoRepository productoRepository;
    private final AbastecimientoRepository abastecimientoRepository;
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    @Value("${app.inventory-cost.estimate-missing-costs:true}")
    private boolean estimateMissingCostsOnRebuild = true;

    @Override
    @Transactional
    public void rebuildCostsFromHistory() {
        List<Producto> productos = productoRepository.findAll();
        Map<Long, BigDecimal> previousRatios = snapshotCostRatios(productos);
        Map<Long, Balance> balances = new HashMap<>();
        for (Producto producto : productos) {
            balances.put(producto.getId(), new Balance(producto));
        }

        List<Movimiento> movimientos = new ArrayList<>();
        for (Abastecimiento abastecimiento : abastecimientoRepository.findAllByOrderByFechaAscIdAsc()) {
            for (DetalleAbastecimiento detalle : abastecimiento.getDetalles()) {
                movimientos.add(Movimiento.abastecimiento(abastecimiento, detalle));
            }
        }
        for (Venta venta : ventaRepository.findAllByOrderByFechaAscIdAsc()) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                movimientos.add(Movimiento.venta(venta, detalle));
            }
        }
        movimientos.sort(Comparator.comparing(Movimiento::fecha)
                .thenComparingInt(Movimiento::orden)
                .thenComparingLong(Movimiento::operacionId)
                .thenComparingLong(Movimiento::detalleId));

        for (Movimiento movimiento : movimientos) {
            Balance balance = balances.get(movimiento.productoId());
            if (balance == null) {
                throw new IllegalStateException("No se encontró el producto asociado al movimiento de inventario.");
            }

            if (movimiento.detalleAbastecimiento() != null) {
                agregarInventario(balance, movimiento.detalleAbastecimiento());
            } else {
                registrarCostoVenta(balance, movimiento.detalleVenta());
            }
        }

        for (Balance balance : balances.values()) {
            if (balance.unidades != balance.producto.getStock() || !balance.costoConocido) {
                log.warn("No se pudo reconstruir el costo promedio del producto id={} ({}): "
                                + "stock histórico={}, stock actual={}. Se conserva el costo registrado.",
                        balance.producto.getId(), balance.producto.getNombre(),
                        balance.unidades, balance.producto.getStock());
            } else if (balance.unidades == 0) {
                balance.producto.setCostoPromedio(ZERO_COST);
            } else {
                balance.producto.setCostoPromedio(balance.costoPromedio);
            }
        }
        if (estimateMissingCostsOnRebuild) {
            estimateMissingCosts(previousRatios);
        }
    }

    @Transactional
    public void estimateMissingCosts() {
        estimateMissingCosts(Map.of());
    }

    private void estimateMissingCosts(Map<Long, BigDecimal> previousRatios) {
        List<Producto> productos = productoRepository.findAll();
        List<DetalleVenta> detallesVenta = detalleVentaRepository.findAll();

        List<BigDecimal> ratiosGlobales = new ArrayList<>();
        Map<UnidadMedida, List<BigDecimal>> ratiosPorUnidad = new HashMap<>();
        for (Producto producto : productos) {
            BigDecimal ratio = ratioCostoPrecio(producto);
            if (ratio == null && producto.getId() != null) {
                ratio = previousRatios.get(producto.getId());
            }
            if (ratio == null) {
                continue;
            }
            ratiosGlobales.add(ratio);
            if (producto.getUnidadMedida() != null) {
                ratiosPorUnidad.computeIfAbsent(producto.getUnidadMedida(), ignored -> new ArrayList<>()).add(ratio);
            }
        }
        Map<UnidadMedida, BigDecimal> medianasPorUnidad = ratiosPorUnidad.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> mediana(entry.getValue())));
        BigDecimal ratioGlobal = ratiosGlobales.isEmpty() ? null : mediana(ratiosGlobales);

        int productosEstimados = 0;
        int productosSinReferencia = 0;
        List<Producto> productosActualizados = new ArrayList<>();
        for (Producto producto : productos) {
            if (producto.getCostoPromedio() != null) {
                continue;
            }
            BigDecimal ratio = ratioPorUnidad(producto.getUnidadMedida(), medianasPorUnidad, ratioGlobal);
            if (ratio == null || producto.getPrecio() == null || producto.getPrecio().signum() <= 0) {
                productosSinReferencia++;
                continue;
            }
            producto.setCostoPromedio(producto.getPrecio().multiply(ratio)
                    .setScale(COST_SCALE, RoundingMode.HALF_UP));
            productosActualizados.add(producto);
            productosEstimados++;
        }

        int detallesEstimados = 0;
        int detallesSinReferencia = 0;
        List<DetalleVenta> detallesActualizados = new ArrayList<>();
        for (DetalleVenta detalle : detallesVenta) {
            if (detalle.getCostoUnitario() != null) {
                continue;
            }
            Producto producto = detalle.getProducto();
            BigDecimal precioUnitario = detalle.getPrecioUnitario();
            BigDecimal ratio = producto == null
                    ? ratioGlobal
                    : ratioPorUnidad(producto.getUnidadMedida(), medianasPorUnidad, ratioGlobal);
            if (ratio == null || precioUnitario == null || precioUnitario.signum() <= 0) {
                detallesSinReferencia++;
                continue;
            }
            detalle.setCostoUnitario(precioUnitario.multiply(ratio)
                    .setScale(COST_SCALE, RoundingMode.HALF_UP));
            detallesActualizados.add(detalle);
            detallesEstimados++;
        }

        productoRepository.saveAll(productosActualizados);
        detalleVentaRepository.saveAll(detallesActualizados);

        if (productosEstimados > 0 || detallesEstimados > 0) {
            log.warn("Estimación temporal de costos aplicada: productos={}, detallesVenta={}; "
                            + "sin referencia: productos={}, detallesVenta={}. Los costos estimados son aproximados.",
                    productosEstimados, detallesEstimados, productosSinReferencia, detallesSinReferencia);
        } else if (productosSinReferencia > 0 || detallesSinReferencia > 0) {
            log.warn("No se pudieron estimar algunos costos faltantes por falta de referencias: "
                            + "productos={}, detallesVenta={}.",
                    productosSinReferencia, detallesSinReferencia);
        }
    }

    private static Map<Long, BigDecimal> snapshotCostRatios(List<Producto> productos) {
        Map<Long, BigDecimal> ratios = new HashMap<>();
        for (Producto producto : productos) {
            BigDecimal ratio = ratioCostoPrecio(producto);
            if (producto.getId() != null && ratio != null) {
                ratios.put(producto.getId(), ratio);
            }
        }
        return ratios;
    }

    private static BigDecimal ratioCostoPrecio(Producto producto) {
        if (producto.getCostoPromedio() == null || producto.getPrecio() == null
                || producto.getCostoPromedio().signum() <= 0 || producto.getPrecio().compareTo(ZERO_PRICE) <= 0) {
            return null;
        }
        return producto.getCostoPromedio().divide(producto.getPrecio(), RATIO_SCALE, RoundingMode.HALF_UP);
    }

    private static BigDecimal ratioPorUnidad(UnidadMedida unidadMedida,
                                              Map<UnidadMedida, BigDecimal> ratiosPorUnidad,
                                              BigDecimal ratioGlobal) {
        return unidadMedida == null ? ratioGlobal : ratiosPorUnidad.getOrDefault(unidadMedida, ratioGlobal);
    }

    private static BigDecimal mediana(List<BigDecimal> valores) {
        List<BigDecimal> ordenados = valores.stream().sorted().toList();
        int medio = ordenados.size() / 2;
        if (ordenados.size() % 2 == 1) {
            return ordenados.get(medio);
        }
        return ordenados.get(medio - 1).add(ordenados.get(medio))
                .divide(BigDecimal.valueOf(2), RATIO_SCALE, RoundingMode.HALF_UP);
    }

    private void agregarInventario(Balance balance, DetalleAbastecimiento detalle) {
        int cantidad = detalle.getCantidad();
        BigDecimal costoCompra = detalle.getCostoUnitario();

        if (balance.unidades < 0) {
            balance.unidades += cantidad;
            if (balance.unidades > 0) {
                balance.costoPromedio = costoCompra.setScale(6, RoundingMode.HALF_UP);
                balance.costoConocido = true;
            } else if (balance.unidades == 0) {
                balance.costoPromedio = ZERO_COST;
                balance.costoConocido = true;
            }
            return;
        }

        if (balance.unidades == 0) {
            balance.costoPromedio = costoCompra.setScale(6, RoundingMode.HALF_UP);
            balance.costoConocido = true;
        } else if (balance.costoConocido) {
            balance.costoPromedio = promedioPonderado(balance.unidades, balance.costoPromedio, cantidad, costoCompra);
        }
        balance.unidades += cantidad;
    }

    public static BigDecimal promedioPonderado(int unidadesActuales, BigDecimal costoActual,
                                                int unidadesNuevas, BigDecimal costoNuevo) {
        if (unidadesActuales < 0 || unidadesNuevas <= 0 || costoActual == null || costoNuevo == null) {
            throw new IllegalArgumentException("Las cantidades y costos deben ser válidos para ponderar el inventario.");
        }
        if (unidadesActuales == 0) {
            return costoNuevo.setScale(6, RoundingMode.HALF_UP);
        }
        return costoActual.multiply(BigDecimal.valueOf(unidadesActuales))
                .add(costoNuevo.multiply(BigDecimal.valueOf(unidadesNuevas)))
                .divide(BigDecimal.valueOf(unidadesActuales + unidadesNuevas), 6, RoundingMode.HALF_UP);
    }

    private void registrarCostoVenta(Balance balance, DetalleVenta detalle) {
        if (balance.unidades >= detalle.getCantidad() && balance.costoConocido) {
            detalle.setCostoUnitario(balance.costoPromedio.setScale(6, RoundingMode.HALF_UP));
        } else {
            detalle.setCostoUnitario(null);
        }

        balance.unidades -= detalle.getCantidad();
        if (balance.unidades == 0) {
            balance.costoPromedio = ZERO_COST;
            balance.costoConocido = true;
        } else if (balance.unidades < 0) {
            balance.costoPromedio = null;
            balance.costoConocido = false;
        }
    }

    private static final class Balance {
        private final Producto producto;
        private int unidades;
        private BigDecimal costoPromedio = ZERO_COST;
        private boolean costoConocido = true;

        private Balance(Producto producto) {
            this.producto = producto;
        }
    }

    private record Movimiento(
            LocalDateTime fecha,
            int orden,
            long operacionId,
            long detalleId,
            Long productoId,
            DetalleAbastecimiento detalleAbastecimiento,
            DetalleVenta detalleVenta) {

        private static Movimiento abastecimiento(Abastecimiento abastecimiento, DetalleAbastecimiento detalle) {
            return new Movimiento(abastecimiento.getFecha(), 0, abastecimiento.getId(), detalle.getId(),
                    detalle.getProducto().getId(), detalle, null);
        }

        private static Movimiento venta(Venta venta, DetalleVenta detalle) {
            return new Movimiento(venta.getFecha(), 1, venta.getId(), detalle.getId(),
                    detalle.getProducto().getId(), null, detalle);
        }
    }
}
