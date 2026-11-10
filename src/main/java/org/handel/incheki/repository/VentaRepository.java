package org.handel.incheki.repository;

import org.handel.incheki.model.Venta;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    Page<Venta> findByClienteId(Long clienteId, Pageable pageable);
}
