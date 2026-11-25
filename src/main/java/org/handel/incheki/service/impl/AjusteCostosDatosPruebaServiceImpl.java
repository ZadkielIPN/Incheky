package org.handel.incheki.service.impl;

import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.response.AjusteCostosDatosPruebaResponse;
import org.handel.incheki.service.AjusteCostosDatosPruebaService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AjusteCostosDatosPruebaServiceImpl implements AjusteCostosDatosPruebaService {
    private static final String TEMP_TABLE = "costos_estimados_venta_prueba";

    private static final String DROP_TEMP_TABLE =
            "DROP TEMPORARY TABLE IF EXISTS " + TEMP_TABLE;

    private static final String CREATE_TEMP_TABLE = """
            CREATE TEMPORARY TABLE costos_estimados_venta_prueba AS
            SELECT
                d.id AS detalle_id,
                v.total AS venta_total,
                totales.subtotal_lineas_venta
            FROM detalles_venta AS d
            JOIN ventas AS v ON v.id = d.venta_id
            JOIN (
                SELECT venta_id, SUM(total) AS subtotal_lineas_venta
                FROM detalles_venta
                GROUP BY venta_id
            ) AS totales ON totales.venta_id = d.venta_id
            """;

    private static final String UPDATE_PRODUCTOS = """
            UPDATE productos
            SET costo_promedio = ROUND(precio * 0.80, 6)
            WHERE precio > 0
            """;

    private static final String UPDATE_DETALLES_VENTA = """
            UPDATE detalles_venta AS d
            JOIN costos_estimados_venta_prueba AS e ON e.detalle_id = d.id
            SET d.costo_unitario = ROUND(
                    (e.venta_total * 0.80 * d.total / NULLIF(e.subtotal_lineas_venta, 0))
                    / d.cantidad,
                    6
                )
            WHERE e.venta_total > 0
              AND e.subtotal_lineas_venta > 0
            """;

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public AjusteCostosDatosPruebaResponse ajustar() {
        jdbcTemplate.execute(DROP_TEMP_TABLE);
        jdbcTemplate.execute(CREATE_TEMP_TABLE);
        try {
            int productosActualizados = jdbcTemplate.update(UPDATE_PRODUCTOS);
            int detallesVentaActualizados = jdbcTemplate.update(UPDATE_DETALLES_VENTA);
            return new AjusteCostosDatosPruebaResponse(productosActualizados, detallesVentaActualizados);
        } finally {
            jdbcTemplate.execute(DROP_TEMP_TABLE);
        }
    }
}
