package org.handel.incheki.service.impl;

import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.DetalleAbastecimientoRequest;
import org.handel.incheki.dtos.response.DetalleAbastecimientoResponse;
import org.handel.incheki.exceptions.ResourcesNotFoundException;
import org.handel.incheki.mappers.DetalleAbastecimientoMapper;
import org.handel.incheki.model.DetalleAbastecimiento;
import org.handel.incheki.repository.DetalleAbastecimientoRepository;
import org.handel.incheki.service.DetalleAbastecimientoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DetalleAbastecimientoServiceImpl implements DetalleAbastecimientoService {
    private final DetalleAbastecimientoRepository detalleAbastecimientoRepository;
    private final DetalleAbastecimientoMapper detalleAbastecimientoMapper;

    @Override
    @Transactional
    public DetalleAbastecimientoResponse create(DetalleAbastecimientoRequest request) {
        DetalleAbastecimiento detalleAbastecimiento = detalleAbastecimientoMapper.toEntity(request);

        detalleAbastecimientoRepository.save(detalleAbastecimiento);
        return detalleAbastecimientoMapper.toResponse(detalleAbastecimiento);
    }

    @Override
    @Transactional(readOnly = true)
    public DetalleAbastecimientoResponse findById(Long id) {
        DetalleAbastecimiento detalleAbastecimiento = findDetalleAbastecimientoById(id);

        return detalleAbastecimientoMapper.toResponse(detalleAbastecimiento);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DetalleAbastecimientoResponse> findAll(Pageable pageable) {
        return detalleAbastecimientoRepository.findAll(pageable)
                .map(detalleAbastecimientoMapper::toResponse);
    }

    @Override
    @Transactional
    public DetalleAbastecimientoResponse update(Long id, DetalleAbastecimientoRequest request) {
        DetalleAbastecimiento detalleAbastecimiento = findDetalleAbastecimientoById(id);

        detalleAbastecimientoMapper.update(request, detalleAbastecimiento);

        detalleAbastecimientoRepository.save(detalleAbastecimiento);
        return detalleAbastecimientoMapper.toResponse(detalleAbastecimiento);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        DetalleAbastecimiento detalleAbastecimiento = findDetalleAbastecimientoById(id);

        detalleAbastecimientoRepository.delete(detalleAbastecimiento);
    }

    private DetalleAbastecimiento findDetalleAbastecimientoById(Long id) {
        return detalleAbastecimientoRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("Detalle Abastecimiento con id " + id + " no encontrado"));
    }
}
