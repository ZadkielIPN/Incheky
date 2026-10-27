package org.handel.incheki.service;

import org.handel.incheki.dtos.request.ProductoRequest;
import org.handel.incheki.dtos.response.ProductoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductoService {
    ProductoResponse create(ProductoRequest request);

    ProductoResponse findById(Long id);

    Page<ProductoResponse> findAll(Pageable pageable);

    ProductoResponse update(Long id, ProductoRequest request);

    void delete(Long id);
}
