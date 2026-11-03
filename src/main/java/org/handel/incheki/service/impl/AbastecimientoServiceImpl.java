package org.handel.incheki.service.impl;

import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.AbastecimientoRequest;
import org.handel.incheki.dtos.response.AbastecimientoResponse;
import org.handel.incheki.exceptions.ResourcesNotFoundException;
import org.handel.incheki.mappers.AbastecimientoMapper;
import org.handel.incheki.model.Abastecimiento;
import org.handel.incheki.repository.AbastecimientoRepository;
import org.handel.incheki.service.AbastecimientoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AbastecimientoServiceImpl implements AbastecimientoService {
    private AbastecimientoRepository abastecimientoRepository;
    private AbastecimientoMapper abastecimientoMapper;

    @Override
    public AbastecimientoResponse create(AbastecimientoRequest request) {

        Abastecimiento abastecimiento =  abastecimientoMapper.toEntity(request);

        abastecimientoRepository.save(abastecimiento);

        return abastecimientoMapper.toResponse(abastecimiento);
    }

    @Override
    public AbastecimientoResponse findById(Long id) {

        Abastecimiento abastecimiento = findAbastecimientoById(id);

        return abastecimientoMapper.toResponse(abastecimiento);
    }

    @Override
    public Page<AbastecimientoResponse> findAll(Pageable pageable) {
        return abastecimientoRepository.findAll(pageable)
                .map(abastecimientoMapper::toResponse);
    }

    @Override
    public AbastecimientoResponse update(Long id, AbastecimientoRequest request) {
        Abastecimiento abastecimiento = findAbastecimientoById(id);

        abastecimientoMapper.update(request, abastecimiento);

        abastecimientoRepository.save(abastecimiento);
        return abastecimientoMapper.toResponse(abastecimiento);
    }

    @Override
    public void delete(Long id) {
        Abastecimiento abastecimiento = findAbastecimientoById(id);

        abastecimientoRepository.delete(abastecimiento);
    }

    private Abastecimiento findAbastecimientoById(Long id) {
        return abastecimientoRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("Abastecimiento con id " + id + " no encontrado"));
    }
}
