package org.handel.incheki.service.impl;

import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.PromocionRequest;
import org.handel.incheki.dtos.response.PromocionResponse;
import org.handel.incheki.exceptions.ResourcesNotFoundException;
import org.handel.incheki.mappers.PromocionMapper;
import org.handel.incheki.model.Promocion;
import org.handel.incheki.repository.PromocionRepository;
import org.handel.incheki.service.PromocionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PromocionServiceImpl implements PromocionService {
    private final PromocionRepository promocionRepository;
    private final PromocionMapper promocionMapper;

    @Override
    @Transactional
    public PromocionResponse create(PromocionRequest request) {
        Promocion promocion = promocionMapper.toEntity(request);

        promocionRepository.save(promocion);
        return promocionMapper.toResponse(promocion);
    }

    @Override
    @Transactional(readOnly = true)
    public PromocionResponse findById(Long id) {
        Promocion promocion = findPromocionById(id);

        return promocionMapper.toResponse(promocion);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PromocionResponse> findAll(Pageable pageable) {
        return promocionRepository.findAll(pageable)
                .map(promocionMapper::toResponse);
    }

    @Override
    @Transactional
    public PromocionResponse update(Long id, PromocionRequest request) {
        Promocion promocion = findPromocionById(id);

        promocionMapper.update(request, promocion);

        promocionRepository.save(promocion);
        return promocionMapper.toResponse(promocion);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Promocion promocion = findPromocionById(id);

        promocionRepository.delete(promocion);
    }

    private Promocion findPromocionById(Long id) {
        return promocionRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("Promocion con id " + id + " no encontrado"));
    }
}
