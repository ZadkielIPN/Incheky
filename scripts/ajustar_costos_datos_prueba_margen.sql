-- SOLO PARA DATOS DE PRUEBA.
-- Estima costo = 80% del precio/ingreso para simular un margen bruto aproximado del 20%.
-- No es un costo real ni debe ejecutarse sobre datos contables o ventas reales.
-- Reemplaza el costo promedio de todos los productos con precio positivo y el costo
-- histórico de todas las líneas de ventas elegibles, aunque ya tengan un costo.
-- Haz un respaldo antes de ejecutarlo.

DROP TEMPORARY TABLE IF EXISTS costos_estimados_venta_prueba;

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
) AS totales ON totales.venta_id = d.venta_id;

START TRANSACTION;

-- Reemplaza todos los costos promedio para que reflejen los precios corregidos.
UPDATE productos
SET costo_promedio = ROUND(precio * 0.80, 6)
WHERE precio > 0;

-- Reemplaza el costo histórico de todas las líneas elegibles.
-- Distribuye el 80% del ingreso neto de cada venta entre sus detalles,
-- incluyendo descuentos de líneas y descuentos globales registrados en ventas.total.
UPDATE detalles_venta AS d
JOIN costos_estimados_venta_prueba AS e ON e.detalle_id = d.id
SET d.costo_unitario = ROUND(
        (e.venta_total * 0.80 * d.total / NULLIF(e.subtotal_lineas_venta, 0))
        / d.cantidad,
        6
    )
WHERE e.venta_total > 0
  AND e.subtotal_lineas_venta > 0;

COMMIT;

DROP TEMPORARY TABLE IF EXISTS costos_estimados_venta_prueba;

-- Revisa el costo promedio actual de los productos.
SELECT id, nombre, precio, costo_promedio, stock
FROM productos
ORDER BY id;

-- Revisa la utilidad bruta estimada por venta después de descuentos.
SELECT
    v.id AS venta_id,
    v.total AS ingresos_netos,
    ROUND(SUM(d.costo_unitario * d.cantidad), 2) AS costo_estimado,
    ROUND(v.total - SUM(d.costo_unitario * d.cantidad), 2) AS utilidad_bruta_estimada
FROM ventas AS v
JOIN detalles_venta AS d ON d.venta_id = v.id
GROUP BY v.id, v.total
ORDER BY v.id;
