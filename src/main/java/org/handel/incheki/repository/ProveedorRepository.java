package org.handel.incheki.repository;

import org.handel.incheki.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    Page<Proveedor> findByNombreContainingIgnoreCaseOrTelefonoContainingIgnoreCaseOrEmailContainingIgnoreCaseOrDireccionContainingIgnoreCase(
            String nombre, String telefono, String email, String direccion, Pageable pageable);
}
