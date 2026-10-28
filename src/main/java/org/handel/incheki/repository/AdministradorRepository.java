package org.handel.incheki.repository;

import org.handel.incheki.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AdministradorRepository extends JpaRepository<Administrador, Long> {
    @Query("select coalesce(max(a.numeroAdministrador), 0) from Administrador a")
    Long findMaxNumeroAdministrador();
}
