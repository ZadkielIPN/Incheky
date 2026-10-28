package org.handel.incheki.service;

import org.handel.incheki.dtos.request.AdministradorRequest;
import org.handel.incheki.dtos.response.AdministradorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdministradorService {
    AdministradorResponse create(AdministradorRequest request);

    AdministradorResponse findById(Long id);

    Page<AdministradorResponse> findAll(Pageable pageable);

    AdministradorResponse update(Long id, AdministradorRequest request);

    Long siguienteNumero();

    void delete(Long id);
}
