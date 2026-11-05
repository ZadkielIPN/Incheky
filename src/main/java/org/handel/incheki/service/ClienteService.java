package org.handel.incheki.service;

import org.handel.incheki.dtos.request.ClienteCreateRequest;
import org.handel.incheki.dtos.request.ClienteUpdateRequest;
import org.handel.incheki.dtos.response.ClienteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClienteService {
    ClienteResponse create(ClienteCreateRequest request);

    ClienteResponse findById(Long id);

    Page<ClienteResponse> findAll(Pageable pageable);

    ClienteResponse update(Long id, ClienteUpdateRequest request);

    void delete(Long id);
}
