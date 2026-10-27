package org.handel.incheki.service.impl;

import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.ProductoRequest;
import org.handel.incheki.dtos.response.ProductoResponse;
import org.handel.incheki.exceptions.ResourcesNotFoundException;
import org.handel.incheki.mappers.ProductoMapper;
import org.handel.incheki.model.Producto;
import org.handel.incheki.repository.ProductoRepository;
import org.handel.incheki.repository.UsuarioRepository;
import org.handel.incheki.service.ProductoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoMapper productoMapper;

    @Override
    @Transactional
    public ProductoResponse create(ProductoRequest request) {
        Producto producto = productoMapper.toEntity(request);

        productoRepository.save(producto);
        return productoMapper.toResponse(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse findById(Long id) {
        Producto producto = findProductoById(id);

        return productoMapper.toResponse(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponse> findAll(Pageable pageable) {
        return productoRepository.findAll(pageable)
                .map(productoMapper::toResponse);
    }

    @Override
    @Transactional
    public ProductoResponse update(Long id, ProductoRequest request) {
        Producto producto = findProductoById(id);

        productoMapper.update(request, producto);

        productoRepository.save(producto);
        return productoMapper.toResponse(producto);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Producto producto = findProductoById(id);

        productoRepository.delete(producto);
    }

    private Producto findProductoById(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("Producto con id " + id + " no encontrado"));
    }
}
