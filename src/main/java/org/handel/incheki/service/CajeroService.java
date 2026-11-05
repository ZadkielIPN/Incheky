package org.handel.incheki.service;

import org.handel.incheki.dtos.request.CajeroCreateRequest;
import org.handel.incheki.dtos.request.CajeroUpdateRequest;
import org.handel.incheki.dtos.response.CajeroResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CajeroService {
    CajeroResponse create(CajeroCreateRequest request);

    CajeroResponse findById(Long id);

    Page<CajeroResponse> findAll(Pageable pageable);

    CajeroResponse update(Long id, CajeroUpdateRequest request);

    void delete(Long id);
}
