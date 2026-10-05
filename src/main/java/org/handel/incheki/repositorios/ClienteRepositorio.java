package org.handel.incheki.repositorios;

import org.handel.incheki.entidades.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepositorio extends JpaRepository<Cliente, Long> {
}
