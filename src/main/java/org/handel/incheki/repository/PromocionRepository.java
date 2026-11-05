package org.handel.incheki.repository;

import org.handel.incheki.model.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PromocionRepository extends JpaRepository<Promocion, Long> {
    Page<Promocion> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
}
