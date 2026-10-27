package org.handel.incheki.service;

import org.handel.incheki.dtos.request.ClienteRequest;
import org.handel.incheki.dtos.response.ClienteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClienteService {
    ClienteResponse create(ClienteRequest request);

    ClienteResponse findById(Long id);

    Page<ClienteResponse> findAll(Pageable pageable);

    ClienteResponse update(Long id, ClienteRequest request);

    void delete(Long id);
}
