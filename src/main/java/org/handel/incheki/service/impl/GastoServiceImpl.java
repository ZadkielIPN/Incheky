package org.handel.incheki.service.impl;

import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.GastoRequest;
import org.handel.incheki.dtos.response.GastoResponse;
import org.handel.incheki.mappers.GastoMapper;
import org.handel.incheki.model.Gasto;
import org.handel.incheki.repository.GastoRepository;
import org.handel.incheki.service.GastoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GastoServiceImpl implements GastoService {
    private final GastoRepository gastoRepository;
    private final GastoMapper gastoMapper;

    @Override
    @Transactional
    public GastoResponse create(GastoRequest request) {
        Gasto gasto = gastoMapper.toEntity(request);
        Gasto guardado = gastoRepository.save(gasto);
        return gastoMapper.toResponse(guardado);
    }
}
