package org.handel.incheki.service;

import org.handel.incheki.dtos.request.DetalleVentaRequest;
import org.handel.incheki.dtos.response.DetalleVentaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DetalleVentaService {
    DetalleVentaResponse create(DetalleVentaRequest request);

    DetalleVentaResponse findById(Long id);

    Page<DetalleVentaResponse> findAll(Pageable pageable);

    DetalleVentaResponse update(Long id, DetalleVentaRequest request);

    void delete(Long id);
}
