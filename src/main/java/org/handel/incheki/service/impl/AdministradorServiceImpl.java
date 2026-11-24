package org.handel.incheki.service.impl;

import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.AdministradorCreateRequest;
import org.handel.incheki.dtos.request.AdministradorUpdateRequest;
import org.handel.incheki.dtos.response.AdministradorResponse;
import org.handel.incheki.model.Administrador;
import org.handel.incheki.enums.Rol;
import org.handel.incheki.exceptions.ResourcesNotFoundException;
import org.handel.incheki.exceptions.EmailDuplicadoException;
import org.handel.incheki.mappers.AdministradorMapper;
import org.handel.incheki.repository.AdministradorRepository;
import org.handel.incheki.repository.UsuarioRepository;
import org.handel.incheki.service.AdministradorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AdministradorServiceImpl implements AdministradorService {
    private final AdministradorRepository administradorRepository;
    private final UsuarioRepository usuarioRepository;
    private final AdministradorMapper administradorMapper;

    @Override
    @Transactional
    public AdministradorResponse create(AdministradorCreateRequest request) {
        String emailNormalizado = normalizarEmail(request.getEmail());
        validarEmailDisponible(emailNormalizado, null);

        Administrador administrador = administradorMapper.toEntity(request);
        administrador.setEmail(emailNormalizado);

        administrador.setRol(Rol.ADMINISTRADOR);
        administrador.setNumeroAdministrador(siguienteNumero());

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
    public Page<AdministradorResponse> findAll(String query, Pageable pageable) {
        Page<Administrador> administradores = query == null || query.isBlank()
                ? administradorRepository.findAll(pageable)
                : administradorRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        query, query, query, pageable);
        return administradores
                .map(administradorMapper::toResponse);
    }

    @Override
    @Transactional
    public AdministradorResponse update(Long id, AdministradorUpdateRequest request) {
        Administrador administrador = findAdministradorById(id);
        String emailNormalizado = normalizarEmail(request.getEmail());
        validarEmailDisponible(emailNormalizado, id);

        administradorMapper.update(request, administrador);
        administrador.setEmail(emailNormalizado);
        if (request.getContrasena() != null && !request.getContrasena().isBlank()) {
            administrador.setContrasena(request.getContrasena());
        }

        administradorRepository.save(administrador);
        return administradorMapper.toResponse(administrador);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Administrador administrador = findAdministradorById(id);

        administradorRepository.delete(administrador);
    }

    @Override
    @Transactional(readOnly = true)
    public Long siguienteNumero() {
        return administradorRepository.findMaxNumeroAdministrador() + 1;
    }

    private Administrador findAdministradorById(Long id) {
        return administradorRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("Administrador con id " + id + " no encontrado"));
    }

    private String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private void validarEmailDisponible(String email, Long idActual) {
        usuarioRepository.findByEmailIgnoreCase(email)
                .filter(existing -> idActual == null || !existing.getId().equals(idActual))
                .ifPresent(existing -> {
                    throw new EmailDuplicadoException("El email " + email + " ya está registrado");
                });
    }
}
