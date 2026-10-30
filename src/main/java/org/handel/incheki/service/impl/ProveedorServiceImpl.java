package org.handel.incheki.service.impl;

import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.ProveedorRequest;
import org.handel.incheki.dtos.response.ProveedorResponse;
import org.handel.incheki.exceptions.ResourcesNotFoundException;
import org.handel.incheki.mappers.ProveedorMapper;
import org.handel.incheki.model.Proveedor;
import org.handel.incheki.repository.ProveedorRepository;
import org.handel.incheki.service.ProveedorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProveedorServiceImpl implements ProveedorService {
    private final ProveedorRepository proveedorRepository;
    private final ProveedorMapper proveedorMapper;

    @Override
    @Transactional
    public ProveedorResponse create(ProveedorRequest request) {
        Proveedor proveedor = proveedorMapper.toEntity(request);

        proveedorRepository.save(proveedor);
        return proveedorMapper.toResponse(proveedor);
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorResponse findById(Long id) {
        Proveedor proveedor = findProveedorById(id);

        return proveedorMapper.toResponse(proveedor);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProveedorResponse> findAll(Pageable pageable) {
        return proveedorRepository.findAll(pageable)
                .map(proveedorMapper::toResponse);
    }

    @Override
    @Transactional
    public ProveedorResponse update(Long id, ProveedorRequest request) {
        Proveedor proveedor = findProveedorById(id);

        proveedorMapper.update(request, proveedor);

        proveedorRepository.save(proveedor);
        return proveedorMapper.toResponse(proveedor);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Proveedor proveedor = findProveedorById(id);

        proveedorRepository.delete(proveedor);
    }

    private Proveedor findProveedorById(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("Proveedor con id " + id + " no encontrado"));
    }
}
