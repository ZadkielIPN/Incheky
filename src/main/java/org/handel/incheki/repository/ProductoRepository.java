package org.handel.incheki.repository;

import org.handel.incheki.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    @Query("""
            select p from Producto p
            where p.activo = true
              and p.stock > 0
              and (
                    :q is null
                    or lower(p.nombre) like lower(concat('%', :q, '%'))
                    or lower(p.descripcion) like lower(concat('%', :q, '%'))
              )
            """)
    Page<Producto> findCatalog(@Param("q") String q, Pageable pageable);

    Page<Producto> findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(
            String nombre, String descripcion, Pageable pageable
    );
}
