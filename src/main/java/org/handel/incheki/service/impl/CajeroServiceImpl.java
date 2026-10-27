package org.handel.incheki.service.impl;

import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.CajeroRequest;
import org.handel.incheki.dtos.response.CajeroResponse;
import org.handel.incheki.enums.Rol;
import org.handel.incheki.exceptions.ResourcesNotFoundException;
import org.handel.incheki.mappers.CajeroMapper;
import org.handel.incheki.model.Cajero;
import org.handel.incheki.repository.CajeroRepository;
import org.handel.incheki.service.CajeroService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CajeroServiceImpl implements CajeroService {
    private final CajeroRepository cajeroRepository;
    private final CajeroMapper cajeroMapper;

    @Override
    @Transactional
    public CajeroResponse create(CajeroRequest request) {
        Cajero cajero = cajeroMapper.toEntity(request);

        cajero.setRol(Rol.ADMINISTRADOR);

        cajeroRepository.save(cajero);
        return cajeroMapper.toResponse(cajero);
    }

    @Override
    @Transactional(readOnly = true)
    public CajeroResponse findById(Long id) {
        Cajero cajero = findCajeroById(id);

        return cajeroMapper.toResponse(cajero);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CajeroResponse> findAll(Pageable pageable) {
        return cajeroRepository.findAll(pageable)
                .map(cajeroMapper::toResponse);
    }

    @Override
    @Transactional
    public CajeroResponse update(Long id, CajeroRequest request) {
        Cajero cajero = findCajeroById(id);

        cajeroMapper.update(request, cajero);

        cajeroRepository.save(cajero);
        return cajeroMapper.toResponse(cajero);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        cajeroRepository.deleteById(id);
    }

    private Cajero findCajeroById(Long id) {
        return cajeroRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("Cajero con id " + id + " no encontrado"));
    }
}
