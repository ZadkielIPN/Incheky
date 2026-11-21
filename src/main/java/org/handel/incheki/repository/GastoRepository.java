package org.handel.incheki.repository;

import org.handel.incheki.model.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GastoRepository extends JpaRepository<Gasto, Long> {
    List<Gasto> findByFechaGreaterThanEqualAndFechaLessThanOrderByFechaAscIdAsc(
            LocalDateTime inicio, LocalDateTime fin);
}
