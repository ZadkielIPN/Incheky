package org.handel.incheki.service;

import org.handel.incheki.dtos.request.VentaRequest;
import org.handel.incheki.dtos.response.VentaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VentaService {
    VentaResponse create(VentaRequest request);

    VentaResponse findById(Long id);

    Page<VentaResponse> findAll(Pageable pageable);

    VentaResponse update(Long id, VentaRequest request);

    void delete(Long id);
}
