package org.handel.incheki.service.impl;

import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.AdministradorRequest;
import org.handel.incheki.dtos.response.AdministradorResponse;
import org.handel.incheki.model.Administrador;
import org.handel.incheki.enums.Rol;
import org.handel.incheki.exceptions.EmailDuplicadoException;
import org.handel.incheki.exceptions.ResourcesNotFoundException;
import org.handel.incheki.mappers.AdministradorMapper;
import org.handel.incheki.repository.AdministradorRepository;
import org.handel.incheki.repository.UsuarioRepository;
import org.handel.incheki.service.AdministradorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdministradorServiceImpl implements AdministradorService {
    private final AdministradorRepository administradorRepository;
    private final UsuarioRepository usuarioRepository;
    private final AdministradorMapper administradorMapper;

    @Override
    @Transactional
    public AdministradorResponse create(AdministradorRequest request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailDuplicadoException("El email " + request.getEmail() + " ya está registrado");
        }

        Administrador administrador = administradorMapper.toEntity(request);

        administrador.setRol(Rol.ADMINISTRADOR);

        administradorRepository.save(administrador);
        return administradorMapper.toResponse(administrador);
    }

    @Override
    @Transactional(readOnly = true)
    public AdministradorResponse findById(Long id) {
        Administrador administrador = findAdministradorById(id);

        return administradorMapper.toResponse(administrador);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdministradorResponse> findAll(Pageable pageable) {
        return administradorRepository.findAll(pageable)
                .map(administradorMapper::toResponse);
    }

    @Override
    @Transactional
    public AdministradorResponse update(Long id, AdministradorRequest request) {
        Administrador administrador = findAdministradorById(id);

        administradorMapper.update(request, administrador);

        administradorRepository.save(administrador);
        return administradorMapper.toResponse(administrador);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Administrador administrador = findAdministradorById(id);

        administradorRepository.delete(administrador);
    }

    private Administrador findAdministradorById(Long id) {
        return administradorRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("Administrador con id " + id + " no encontrado"));
    }
}
