package org.handel.incheki.service;

import org.handel.incheki.dtos.request.ProductoRequest;
import org.handel.incheki.dtos.response.ProductoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ProductoService {
    ProductoResponse create(ProductoRequest request, MultipartFile file);

    ProductoResponse findById(Long id);

    Page<ProductoResponse> findAll(Pageable pageable);

    Page<ProductoResponse> findAll(String q, Pageable pageable);

    Page<ProductoResponse> findCatalog(String q, Pageable pageable);

    ProductoResponse update(Long id, ProductoRequest request, MultipartFile file);

    void delete(Long id);
}
