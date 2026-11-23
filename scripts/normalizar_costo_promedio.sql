-- Corrige los costos existentes antes de hacer la columna obligatoria.
START TRANSACTION;

UPDATE productos
SET costo_promedio = 0.000000
WHERE costo_promedio IS NULL;

COMMIT;

-- También evita que nuevas filas reciban NULL si la aplicación omite el campo.
ALTER TABLE productos
    MODIFY COLUMN costo_promedio DECIMAL(12, 6) NOT NULL DEFAULT 0.000000;

-- Debe devolver 0 después de ejecutar la actualización.
SELECT COUNT(*) AS productos_sin_costo_promedio
FROM productos
WHERE costo_promedio IS NULL;
