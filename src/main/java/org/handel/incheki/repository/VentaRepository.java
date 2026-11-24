package org.handel.incheki.repository;

import org.handel.incheki.model.Venta;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    Page<Venta> findByClienteId(Long clienteId, Pageable pageable);

    java.util.List<Venta> findAllByOrderByFechaAscIdAsc();

    java.util.List<Venta> findByFechaGreaterThanEqualAndFechaLessThanOrderByFechaAscIdAsc(
            java.time.LocalDateTime inicio, java.time.LocalDateTime fin);

    @Query("""
            select distinct v
            from Venta v
            left join v.cliente cliente
            join v.cajero cajero
            left join v.detalles detalle
            left join detalle.producto producto
            where
              lower(concat(coalesce(cliente.nombre, ''), ' ', coalesce(cliente.apellido, '')))
                like lower(concat('%', :q, '%'))
              or lower(concat(coalesce(cajero.nombre, ''), ' ', coalesce(cajero.apellido, '')))
                like lower(concat('%', :q, '%'))
              or lower(coalesce(producto.nombre, ''))
                like lower(concat('%', :q, '%'))
              or lower(coalesce(producto.codigoBarras, ''))
                like lower(concat('%', :q, '%'))
            """)
    Page<Venta> search(@Param("q") String q, Pageable pageable);
}
