-- Antes de ejecutar, guarda una copia de seguridad de la base de datos.
-- Calcula el costo promedio ponderado por producto usando todos sus abastecimientos.
-- Los productos sin historial de abastecimiento quedan sin cambios.
START TRANSACTION;

UPDATE productos AS p
JOIN (
    SELECT
        producto_id,
        ROUND(
            SUM(cantidad * costo_unitario) / NULLIF(SUM(cantidad), 0),
            6
        ) AS costo_promedio_calculado
    FROM detalles_abastecimiento
    GROUP BY producto_id
) AS compras ON compras.producto_id = p.id
SET p.costo_promedio = compras.costo_promedio_calculado;

COMMIT;

-- Productos cuyo historial no explica el stock actual, suponiendo inventario inicial 0.
-- En estos casos el costo calculado es orientativo: puede faltar el inventario de apertura.
SELECT
    p.id,
    p.nombre,
    p.stock AS stock_actual,
    COALESCE(compras.unidades_abastecidas, 0) AS unidades_abastecidas,
    COALESCE(ventas.unidades_vendidas, 0) AS unidades_vendidas,
    COALESCE(compras.unidades_abastecidas, 0)
        - COALESCE(ventas.unidades_vendidas, 0) AS stock_segun_historial,
    p.costo_promedio
FROM productos AS p
LEFT JOIN (
    SELECT producto_id, SUM(cantidad) AS unidades_abastecidas
    FROM detalles_abastecimiento
    GROUP BY producto_id
) AS compras ON compras.producto_id = p.id
LEFT JOIN (
    SELECT producto_id, SUM(cantidad) AS unidades_vendidas
    FROM detalles_venta
    GROUP BY producto_id
) AS ventas ON ventas.producto_id = p.id
WHERE p.stock <> COALESCE(compras.unidades_abastecidas, 0)
                - COALESCE(ventas.unidades_vendidas, 0)
ORDER BY p.id;

-- Productos para los que no existe un costo de compra que se pueda calcular automáticamente.
SELECT p.id, p.nombre, p.stock, p.costo_promedio
FROM productos AS p
WHERE NOT EXISTS (
    SELECT 1
    FROM detalles_abastecimiento AS d
    WHERE d.producto_id = p.id
)
ORDER BY p.id;
