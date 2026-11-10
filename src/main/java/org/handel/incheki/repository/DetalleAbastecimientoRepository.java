package org.handel.incheki.repository;

import org.handel.incheki.model.DetalleAbastecimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleAbastecimientoRepository extends JpaRepository<DetalleAbastecimiento, Long> {
}
