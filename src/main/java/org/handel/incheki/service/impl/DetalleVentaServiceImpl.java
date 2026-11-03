package org.handel.incheki.service.impl;

import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.DetalleVentaRequest;
import org.handel.incheki.dtos.response.DetalleVentaResponse;
import org.handel.incheki.exceptions.ResourcesNotFoundException;
import org.handel.incheki.mappers.DetalleVentaMapper;
import org.handel.incheki.model.DetalleVenta;
import org.handel.incheki.repository.DetalleVentaRepository;
import org.handel.incheki.service.DetalleVentaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DetalleVentaServiceImpl implements DetalleVentaService {
    private final DetalleVentaRepository detalleVentaRepository;
    private final DetalleVentaMapper detalleVentaMapper;

    @Override
    @Transactional
    public DetalleVentaResponse create(DetalleVentaRequest request) {
        DetalleVenta detalleVenta = detalleVentaMapper.toEntity(request);

        detalleVentaRepository.save(detalleVenta);
        return detalleVentaMapper.toResponse(detalleVenta);
    }

    @Override
    @Transactional(readOnly = true)
    public DetalleVentaResponse findById(Long id) {
        DetalleVenta detalleVenta = findDetalleVentaById(id);

        return detalleVentaMapper.toResponse(detalleVenta);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DetalleVentaResponse> findAll(Pageable pageable) {
        return detalleVentaRepository.findAll(pageable)
                .map(detalleVentaMapper::toResponse);
    }

    @Override
    @Transactional
    public DetalleVentaResponse update(Long id, DetalleVentaRequest request) {
        DetalleVenta detalleVenta = findDetalleVentaById(id);

        detalleVentaMapper.update(request, detalleVenta);

        detalleVentaRepository.save(detalleVenta);
        return detalleVentaMapper.toResponse(detalleVenta);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        DetalleVenta detalleVenta = findDetalleVentaById(id);

        detalleVentaRepository.delete(detalleVenta);
    }

    private DetalleVenta findDetalleVentaById(Long id) {
        return detalleVentaRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("DetalleVenta con id " + id + " no encontrado"));
    }
}
