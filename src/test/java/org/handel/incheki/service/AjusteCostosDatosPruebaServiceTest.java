package org.handel.incheki.service;

import org.handel.incheki.dtos.response.AjusteCostosDatosPruebaResponse;
import org.handel.incheki.service.impl.AjusteCostosDatosPruebaServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AjusteCostosDatosPruebaServiceTest {
    private final JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    private final AjusteCostosDatosPruebaService service =
            new AjusteCostosDatosPruebaServiceImpl(jdbcTemplate);

    @Test
    void recalculatesProductAndHistoricalCostsAndReturnsUpdatedRowCounts() {
        when(jdbcTemplate.update(ArgumentMatchers.<String>argThat(
                sql -> sql != null && sql.contains("UPDATE productos"))))
                .thenReturn(3);
        when(jdbcTemplate.update(ArgumentMatchers.<String>argThat(
                sql -> sql != null && sql.contains("UPDATE detalles_venta"))))
                .thenReturn(8);

        AjusteCostosDatosPruebaResponse response = service.ajustar();

        assertEquals(3, response.productosActualizados());
        assertEquals(8, response.detallesVentaActualizados());
        verify(jdbcTemplate).update(ArgumentMatchers.<String>argThat(
                sql -> sql.contains("SET costo_promedio = ROUND(precio * 0.80, 6)")
                        && sql.contains("WHERE precio > 0")));
        verify(jdbcTemplate).update(ArgumentMatchers.<String>argThat(
                sql -> sql.contains("SET d.costo_unitario = ROUND(")
                        && sql.contains("e.venta_total > 0")
                        && sql.contains("e.subtotal_lineas_venta > 0")
                        && !sql.contains("d.costo_unitario IS NULL")));
    }
}
