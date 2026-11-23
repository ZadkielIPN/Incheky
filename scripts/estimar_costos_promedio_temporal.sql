-- Estimacion temporal para productos de prueba.
-- Usa el precio de venta actual como costo solo si el costo promedio no existe o es 0.
-- No modifica abastecimientos, ventas ni gastos. El precio de venta no es el costo real.
-- Haz un respaldo de la base de datos antes de ejecutar.
START TRANSACTION;

UPDATE productos
SET costo_promedio = precio
WHERE (costo_promedio IS NULL OR costo_promedio = 0)
  AND precio > 0;

SELECT ROW_COUNT() AS productos_actualizados;

COMMIT;

-- Productos que siguen sin un costo promedio positivo y requieren revision.
SELECT id, nombre, precio, stock, costo_promedio
FROM productos
WHERE costo_promedio IS NULL
   OR costo_promedio = 0
   OR precio = 0
ORDER BY id;
