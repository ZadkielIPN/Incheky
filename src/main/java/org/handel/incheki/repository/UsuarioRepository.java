package org.handel.incheki.repository;

import org.handel.incheki.model.Usuario;
import org.handel.incheki.enums.Rol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Page<Usuario> findAllByRol(Rol rol, Pageable paginable);

    Optional<Usuario> findByEmail(String email);

    @Query("""
            select coalesce(max(c.numeroEmpleado), 0)
            from Cajero c
            """)
    Long findMaxNumeroEmpleado();

    Page<Usuario> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String nombre, String apellido, String email, Pageable pageable);

}
