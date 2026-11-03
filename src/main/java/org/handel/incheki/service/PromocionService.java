package org.handel.incheki.service;

import org.handel.incheki.dtos.request.PromocionRequest;
import org.handel.incheki.dtos.response.PromocionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface PromocionService {
    PromocionResponse create(PromocionRequest request);

    PromocionResponse findById(Long id);

    Page<PromocionResponse> findAll(Pageable pageable);

    PromocionResponse update(Long id, PromocionRequest request);

    void delete(Long id);
}
