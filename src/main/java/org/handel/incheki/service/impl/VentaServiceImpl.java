package org.handel.incheki.service.impl;

import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.VentaRequest;
import org.handel.incheki.dtos.response.VentaResponse;
import org.handel.incheki.exceptions.ResourcesNotFoundException;
import org.handel.incheki.mappers.VentaMapper;
import org.handel.incheki.model.Venta;
import org.handel.incheki.repository.VentaRepository;
import org.handel.incheki.service.VentaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {
    private final VentaRepository ventaRepository;
    private final VentaMapper ventaMapper;

    @Override
    @Transactional
    public VentaResponse create(VentaRequest request) {
        Venta venta = ventaMapper.toEntity(request);

        ventaRepository.save(venta);
        return ventaMapper.toResponse(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public VentaResponse findById(Long id) {
        Venta venta = findVentaById(id);

        return ventaMapper.toResponse(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaResponse> findAll(Pageable pageable) {
        return ventaRepository.findAll(pageable)
                .map(ventaMapper::toResponse);
    }

    @Override
    @Transactional
    public VentaResponse update(Long id, VentaRequest request) {
        Venta venta = findVentaById(id);

        ventaMapper.update(request, venta);

        ventaRepository.save(venta);
        return ventaMapper.toResponse(venta);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Venta venta = findVentaById(id);

        ventaRepository.delete(venta);
    }

    private Venta findVentaById(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("Venta con id " + id + " no encontrado"));
    }
}
