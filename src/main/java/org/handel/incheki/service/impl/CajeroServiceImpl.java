package org.handel.incheki.service.impl;

import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.CajeroCreateRequest;
import org.handel.incheki.dtos.request.CajeroUpdateRequest;
import org.handel.incheki.dtos.response.CajeroResponse;
import org.handel.incheki.enums.Rol;
import org.handel.incheki.exceptions.ResourcesNotFoundException;
import org.handel.incheki.exceptions.EmailDuplicadoException;
import org.handel.incheki.mappers.CajeroMapper;
import org.handel.incheki.model.Cajero;
import org.handel.incheki.repository.CajeroRepository;
import org.handel.incheki.repository.UsuarioRepository;
import org.handel.incheki.service.CajeroService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CajeroServiceImpl implements CajeroService {
    private final CajeroRepository cajeroRepository;
    private final CajeroMapper cajeroMapper;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public CajeroResponse create(CajeroCreateRequest request) {
        String emailNormalizado = normalizarEmail(request.getEmail());
        validarEmailDisponible(emailNormalizado, null);
        Cajero cajero = cajeroMapper.toEntity(request);
        cajero.setEmail(emailNormalizado);

        cajero.setRol(Rol.CAJERO);
        cajero.setNumeroEmpleado(siguienteNumero());

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
    public Page<CajeroResponse> findAll(String query, Pageable pageable) {
        Page<Cajero> cajeros = query == null || query.isBlank()
                ? cajeroRepository.findAll(pageable)
                : cajeroRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        query, query, query, pageable);
        return cajeros
                .map(cajeroMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CajeroResponse findByUidNfc(String uidNfc) {
        if (uidNfc == null || uidNfc.trim().isEmpty()) {
            throw new org.handel.incheki.exceptions.UidNfcInvalidoException(
                    "El UID NFC no puede estar vacío");
        }

        String uidNormalizado = uidNfc.trim();
        if (uidNormalizado.length() > 255) {
            throw new org.handel.incheki.exceptions.UidNfcInvalidoException(
                    "El UID NFC no puede superar 255 caracteres");
        }

        Cajero cajero = cajeroRepository.findByUidNfc(uidNormalizado)
                .orElseThrow(() -> new ResourcesNotFoundException("No se encontró un cajero con esa tarjeta NFC"));
        return cajeroMapper.toResponse(cajero);
    }

    @Override
    @Transactional
    public CajeroResponse update(Long id, CajeroUpdateRequest request) {
        Cajero cajero = findCajeroById(id);
        String emailNormalizado = normalizarEmail(request.getEmail());
        validarEmailDisponible(emailNormalizado, id);

        cajeroMapper.update(request, cajero);
        cajero.setEmail(emailNormalizado);
        if (request.getContrasena() != null && !request.getContrasena().isBlank()) {
            cajero.setContrasena(request.getContrasena());
        }

        cajeroRepository.save(cajero);
        return cajeroMapper.toResponse(cajero);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Cajero cajero = findCajeroById(id);

        cajeroRepository.delete(cajero);
    }

    private Cajero findCajeroById(Long id) {
        return cajeroRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("Cajero con id " + id + " no encontrado"));
    }

    private Long siguienteNumero() {
        return usuarioRepository.findMaxNumeroEmpleado() + 1;
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
