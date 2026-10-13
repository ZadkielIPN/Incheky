package org.handel.incheki.services.impl;

import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.UsuarioSession;
import org.handel.incheki.dtos.response.UsuarioResponse;
import org.handel.incheki.entities.Usuario;
import org.handel.incheki.enums.Rol;
import org.handel.incheki.exceptions.ResourcesNotFoundException;
import org.handel.incheki.mappers.UsuarioMapper;
import org.handel.incheki.repository.UsuarioRepository;
import org.handel.incheki.services.UsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourcesNotFoundException("Email o contraseña incorrectos"));

        if (!usuario.getContrasena().equals(contrasena)) {
            throw new ResourcesNotFoundException("Email o contraseña incorrectos");
        }

        return usuarioMapper.toSession(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponse> findAll(Rol rol, Pageable paginable) {
        Page<Usuario> users;

        if (rol != null) {
            users = usuarioRepository.findAllByRol(rol, paginable);
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
}
