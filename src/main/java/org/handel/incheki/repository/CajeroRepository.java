package org.handel.incheki.repository;

import org.handel.incheki.model.Cajero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CajeroRepository extends JpaRepository<Cajero, Long> {
    Page<Cajero> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String nombre, String apellido, String email, Pageable pageable);
}
