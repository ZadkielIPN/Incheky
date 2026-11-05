package org.handel.incheki.repository;

import org.handel.incheki.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdministradorRepository extends JpaRepository<Administrador, Long> {
    Page<Administrador> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String nombre, String apellido, String email, Pageable pageable);
    @Query("select coalesce(max(a.numeroAdministrador), 0) from Administrador a")
    Long findMaxNumeroAdministrador();
}
