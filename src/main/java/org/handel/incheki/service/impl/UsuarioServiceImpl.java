package org.handel.incheki.service.impl;

import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.UsuarioProfileRequest;
import org.handel.incheki.dtos.UsuarioSession;
import org.handel.incheki.dtos.response.UsuarioResponse;
import org.handel.incheki.model.Usuario;
import org.handel.incheki.enums.Rol;
import org.handel.incheki.exceptions.ResourcesNotFoundException;
import org.handel.incheki.mappers.UsuarioMapper;
import org.handel.incheki.repository.UsuarioRepository;
import org.handel.incheki.service.UsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse findById(Long id) {
        Usuario user = findUserById(id);

        return usuarioMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioSession login(String email, String contrasena) {
        String emailNormalizado = normalizarEmail(email);
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(emailNormalizado)
                .orElseThrow(() -> new ResourcesNotFoundException("Email o contraseña incorrectos"));

        if (!usuario.getContrasena().equals(contrasena)) {
            throw new ResourcesNotFoundException("Email o contraseña incorrectos");
        }

        return usuarioMapper.toSession(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioSession loginNfc(String uidNfc) {
        String uidNormalizado = validarUidNfc(uidNfc);
        Usuario usuario = usuarioRepository.findByUidNfc(uidNormalizado)
                .orElseThrow(() -> new ResourcesNotFoundException("Tarjeta NFC no registrada"));

        return usuarioMapper.toSession(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponse asignarTarjetaNfc(Long id, String uidNfc) {
        Usuario usuario = findUserById(id);
        if (uidNfc == null || uidNfc.trim().isEmpty()) {
            usuario.setUidNfc(null);
            return usuarioMapper.toResponse(usuarioRepository.save(usuario));
        }

        String uidNormalizado = validarUidNfc(uidNfc);

        usuarioRepository.findByUidNfc(uidNormalizado)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new org.handel.incheki.exceptions.UidNfcDuplicadoException(
                            "El UID NFC " + uidNormalizado + " ya está asignado a otro usuario");
                });

        usuario.setUidNfc(uidNormalizado);
        return usuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public UsuarioResponse updateProfile(Long id, UsuarioProfileRequest request) {
        Usuario usuario = findUserById(id);
        String emailNormalizado = normalizarEmail(request.getEmail());

        usuarioRepository.findByEmailIgnoreCase(emailNormalizado)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new org.handel.incheki.exceptions.EmailDuplicadoException(
                            "El email " + emailNormalizado + " ya está registrado");
                });

        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(emailNormalizado);

        if (request.getContrasena() != null && !request.getContrasena().isBlank()) {
            usuario.setContrasena(request.getContrasena());
        }

        return usuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponse> findAll(Rol rol, String query, Pageable paginable) {
        Page<Usuario> users;

        if (rol != null) {
            users = usuarioRepository.findAllByRol(rol, paginable);
        } else if (query != null && !query.isBlank()) {
            users = usuarioRepository
                    .findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrEmailContainingIgnoreCase(
                            query.trim(), query.trim(), query.trim(), paginable);
        } else {
            users = usuarioRepository.findAll(paginable);
        }

        return users.map(usuarioMapper::toResponse);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Usuario user = findUserById(id);

        usuarioRepository.delete(user);
    }

    private Usuario findUserById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("Usuario con id " + id + " no encontrado"));
    }

    private String validarUidNfc(String uidNfc) {
        if (uidNfc == null || uidNfc.trim().isEmpty()) {
            throw new org.handel.incheki.exceptions.UidNfcInvalidoException(
                    "El UID NFC no puede estar vacío");
        }

        String uidNormalizado = uidNfc.trim();
        if (uidNormalizado.length() > 255) {
            throw new org.handel.incheki.exceptions.UidNfcInvalidoException(
                    "El UID NFC no puede superar 255 caracteres");
        }

        return uidNormalizado;
    }

    private String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
