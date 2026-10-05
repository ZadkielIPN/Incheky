package org.handel.incheki.repositorios;

import org.handel.incheki.entidades.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepositorio extends JpaRepository<Producto, Long> {
}
