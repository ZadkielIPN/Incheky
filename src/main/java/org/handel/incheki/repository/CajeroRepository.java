package org.handel.incheki.repository;

import org.handel.incheki.model.Cajero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CajeroRepository extends JpaRepository<Cajero, Long> {
    Page<Cajero> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String nombre, String apellido, String email, Pageable pageable);

    Optional<Cajero> findByUidNfc(String uidNfc);
}
