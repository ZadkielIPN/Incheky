package org.handel.incheki.service;

import org.handel.incheki.dtos.request.DetalleAbastecimientoRequest;
import org.handel.incheki.dtos.response.DetalleAbastecimientoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DetalleAbastecimientoService {
    DetalleAbastecimientoResponse create(DetalleAbastecimientoRequest request);

    DetalleAbastecimientoResponse findById(Long id);

    Page<DetalleAbastecimientoResponse> findAll(Pageable pageable);

    DetalleAbastecimientoResponse update(Long id, DetalleAbastecimientoRequest request);

    void delete(Long id);
}
