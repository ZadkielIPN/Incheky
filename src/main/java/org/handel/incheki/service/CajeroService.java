package org.handel.incheki.service;

import org.handel.incheki.dtos.request.CajeroRequest;
import org.handel.incheki.dtos.response.CajeroResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CajeroService {
    CajeroResponse create(CajeroRequest request);

    CajeroResponse findById(Long id);

    Page<CajeroResponse> findAll(Pageable pageable);

    CajeroResponse update(Long id, CajeroRequest request);

    void delete(Long id);
}
