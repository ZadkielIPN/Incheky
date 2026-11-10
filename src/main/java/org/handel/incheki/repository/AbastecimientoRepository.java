package org.handel.incheki.repository;

import org.handel.incheki.model.Abastecimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AbastecimientoRepository extends JpaRepository<Abastecimiento, Long> {
}
