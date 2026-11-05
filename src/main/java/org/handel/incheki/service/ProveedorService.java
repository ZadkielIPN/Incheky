package org.handel.incheki.service;

import org.handel.incheki.dtos.request.ProveedorRequest;
import org.handel.incheki.dtos.response.ProveedorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProveedorService {
    ProveedorResponse create(ProveedorRequest request);

    ProveedorResponse findById(Long id);

    Page<ProveedorResponse> findAll(String query, Pageable pageable);

    ProveedorResponse update(Long id, ProveedorRequest request);

    void delete(Long id);
}
