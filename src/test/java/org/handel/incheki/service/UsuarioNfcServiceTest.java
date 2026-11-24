package org.handel.incheki.service;

import org.handel.incheki.dtos.UsuarioSession;
import org.handel.incheki.enums.Rol;
import org.handel.incheki.dtos.response.UsuarioResponse;
import org.handel.incheki.exceptions.ResourcesNotFoundException;
import org.handel.incheki.exceptions.UidNfcDuplicadoException;
import org.handel.incheki.mappers.UsuarioMapper;
import org.handel.incheki.model.Usuario;
import org.handel.incheki.repository.UsuarioRepository;
import org.handel.incheki.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UsuarioNfcServiceTest {

    private UsuarioRepository usuarioRepository;
    private UsuarioMapper usuarioMapper;
    private UsuarioServiceImpl usuarioService;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        usuarioMapper = mock(UsuarioMapper.class);
        usuarioService = new UsuarioServiceImpl(usuarioRepository, usuarioMapper);
    }

    @Test
    void asignarTarjetaConservaCerosIzquierda() {
        Usuario usuario = mock(Usuario.class);
        UsuarioResponse response = new UsuarioResponse();
        response.setUidNfc("0011522182");

        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByUidNfc("0011522182")).thenReturn(Optional.empty());
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        when(usuarioMapper.toResponse(usuario)).thenReturn(response);

        UsuarioResponse result = usuarioService.asignarTarjetaNfc(10L, " 0011522182 ");

        assertEquals("0011522182", result.getUidNfc());
        verify(usuario).setUidNfc("0011522182");
    }

    @Test
    void rechazaUidAsignadoAOtroUsuario() {
        Usuario usuario = mock(Usuario.class);
        Usuario otroUsuario = mock(Usuario.class);

        when(usuario.getId()).thenReturn(10L);
        when(otroUsuario.getId()).thenReturn(20L);
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByUidNfc("0009580011")).thenReturn(Optional.of(otroUsuario));

        assertThrows(UidNfcDuplicadoException.class,
                () -> usuarioService.asignarTarjetaNfc(10L, "0009580011"));
    }

    @Test
    void permiteQuitarTarjetaConUidVacio() {
        Usuario usuario = mock(Usuario.class);
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        when(usuarioMapper.toResponse(usuario)).thenReturn(new UsuarioResponse());

        usuarioService.asignarTarjetaNfc(10L, " ");

        verify(usuario).setUidNfc(null);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void iniciaSesionPorUidRegistrado() {
        Usuario usuario = mock(Usuario.class);
        UsuarioSession session = new UsuarioSession(
                10L, "Ada", "Lovelace", "ada@incheky.com", null, Rol.CAJERO);

        when(usuarioRepository.findByUidNfc("0011522182")).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toSession(usuario)).thenReturn(session);

        assertEquals(session, usuarioService.loginNfc("0011522182"));
    }

    @Test
    void rechazaUidDesconocido() {
        when(usuarioRepository.findByUidNfc("0000000000")).thenReturn(Optional.empty());

        assertThrows(ResourcesNotFoundException.class,
                () -> usuarioService.loginNfc("0000000000"));
    }
}
