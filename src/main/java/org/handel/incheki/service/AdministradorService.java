package org.handel.incheki.service;

import org.handel.incheki.dtos.request.AdministradorCreateRequest;
import org.handel.incheki.dtos.request.AdministradorUpdateRequest;
import org.handel.incheki.dtos.response.AdministradorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdministradorService {
    AdministradorResponse create(AdministradorCreateRequest request);

    AdministradorResponse findById(Long id);

    Page<AdministradorResponse> findAll(Pageable pageable);

    AdministradorResponse update(Long id, AdministradorUpdateRequest request);

    Long siguienteNumero();

    void delete(Long id);
}
