package org.handel.incheki.service;

import org.handel.incheki.dtos.request.AbastecimientoRequest;
import org.handel.incheki.dtos.response.AbastecimientoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AbastecimientoService {
    AbastecimientoResponse create(AbastecimientoRequest request);

    AbastecimientoResponse findById(Long id);

    Page<AbastecimientoResponse> findAll(Pageable pageable);

    AbastecimientoResponse update(Long id, AbastecimientoRequest request);

    void delete(Long id);
}
