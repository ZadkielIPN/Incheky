package org.handel.incheki.repository;

import org.handel.incheki.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Page<Cliente> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String nombre, String apellido, String email, Pageable pageable);

    Optional<Cliente> findByUidNfc(String uidNfc);
}
