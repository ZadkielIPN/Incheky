package org.handel.incheki.service;

import org.handel.incheki.dtos.request.AdministradorCreateRequest;
import org.handel.incheki.dtos.request.CajeroCreateRequest;
import org.handel.incheki.dtos.request.ClienteCreateRequest;
import org.handel.incheki.exceptions.EmailDuplicadoException;
import org.handel.incheki.mappers.AdministradorMapper;
import org.handel.incheki.mappers.CajeroMapper;
import org.handel.incheki.mappers.ClienteMapper;
import org.handel.incheki.model.Administrador;
import org.handel.incheki.model.Cajero;
import org.handel.incheki.model.Cliente;
import org.handel.incheki.model.Usuario;
import org.handel.incheki.repository.AdministradorRepository;
import org.handel.incheki.repository.CajeroRepository;
import org.handel.incheki.repository.ClienteRepository;
import org.handel.incheki.repository.UsuarioRepository;
import org.handel.incheki.service.impl.AdministradorServiceImpl;
import org.handel.incheki.service.impl.CajeroServiceImpl;
import org.handel.incheki.service.impl.ClienteServiceImpl;
import org.handel.incheki.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EmailDuplicadoServiceTest {

    @Test
    void rechazaClienteDuplicadoSinDistinguirMayusculas() {
        ClienteRepository clienteRepository = mock(ClienteRepository.class);
        ClienteMapper clienteMapper = mock(ClienteMapper.class);
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        Cliente cliente = mock(Cliente.class);
        Usuario existente = mock(Usuario.class);
        ClienteCreateRequest request = new ClienteCreateRequest();
        request.setEmail("  Nuevo@Ejemplo.com ");

        when(usuarioRepository.findByEmailIgnoreCase("nuevo@ejemplo.com"))
                .thenReturn(Optional.of(existente));
        when(clienteMapper.toEntity(request)).thenReturn(cliente);

        ClienteServiceImpl service =
                new ClienteServiceImpl(clienteRepository, clienteMapper, usuarioRepository);

        assertThrows(EmailDuplicadoException.class, () -> service.create(request));
    }

    @Test
    void rechazaCajeroDuplicado() {
        CajeroRepository cajeroRepository = mock(CajeroRepository.class);
        CajeroMapper cajeroMapper = mock(CajeroMapper.class);
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        Usuario existente = mock(Usuario.class);
        CajeroCreateRequest request = new CajeroCreateRequest();
        request.setEmail("cajero@ejemplo.com");

        when(usuarioRepository.findByEmailIgnoreCase("cajero@ejemplo.com"))
                .thenReturn(Optional.of(existente));

        CajeroServiceImpl service =
                new CajeroServiceImpl(cajeroRepository, cajeroMapper, usuarioRepository);

        assertThrows(EmailDuplicadoException.class, () -> service.create(request));
    }

    @Test
    void rechazaAdministradorDuplicado() {
        AdministradorRepository administradorRepository = mock(AdministradorRepository.class);
        AdministradorMapper administradorMapper = mock(AdministradorMapper.class);
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        Usuario existente = mock(Usuario.class);
        AdministradorCreateRequest request = new AdministradorCreateRequest();
        request.setEmail("admin@ejemplo.com");

        when(usuarioRepository.findByEmailIgnoreCase("admin@ejemplo.com"))
                .thenReturn(Optional.of(existente));

        AdministradorServiceImpl service =
                new AdministradorServiceImpl(
                        administradorRepository, usuarioRepository, administradorMapper);

        assertThrows(EmailDuplicadoException.class, () -> service.create(request));
    }

    @Test
    void permiteActualizarUsuarioAlConservarSuPropioEmail() {
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        Usuario usuario = mock(Usuario.class);
        when(usuario.getId()).thenReturn(10L);
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByEmailIgnoreCase("propio@ejemplo.com"))
                .thenReturn(Optional.of(usuario));

        org.handel.incheki.mappers.UsuarioMapper usuarioMapper =
                mock(org.handel.incheki.mappers.UsuarioMapper.class);
        UsuarioServiceImpl service =
                new UsuarioServiceImpl(usuarioRepository, usuarioMapper);
        org.handel.incheki.dtos.request.UsuarioProfileRequest request =
                new org.handel.incheki.dtos.request.UsuarioProfileRequest();
        request.setNombre("Usuario");
        request.setApellido("Propio");
        request.setEmail(" PROPIO@Ejemplo.com ");

        service.updateProfile(10L, request);
    }
}
