package org.handel.incheki.service;

import org.handel.incheki.dtos.response.ClienteResponse;
import org.handel.incheki.exceptions.ResourcesNotFoundException;
import org.handel.incheki.exceptions.UidNfcInvalidoException;
import org.handel.incheki.mappers.ClienteMapper;
import org.handel.incheki.model.Cliente;
import org.handel.incheki.repository.ClienteRepository;
import org.handel.incheki.repository.UsuarioRepository;
import org.handel.incheki.service.impl.ClienteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClienteNfcServiceTest {

    private ClienteRepository clienteRepository;
    private ClienteMapper clienteMapper;
    private ClienteServiceImpl clienteService;

    @BeforeEach
    void setUp() {
        clienteRepository = mock(ClienteRepository.class);
        clienteMapper = mock(ClienteMapper.class);
        clienteService = new ClienteServiceImpl(
                clienteRepository, clienteMapper, mock(UsuarioRepository.class));
    }

    @Test
    void buscaClienteConUidExactoYConservaCerosIzquierda() {
        Cliente cliente = mock(Cliente.class);
        ClienteResponse response = new ClienteResponse();
        response.setUidNfc("0011522182");

        when(clienteRepository.findByUidNfc("0011522182")).thenReturn(Optional.of(cliente));
        when(clienteMapper.toResponse(cliente)).thenReturn(response);

        assertEquals(response, clienteService.findByUidNfc(" 0011522182 "));
    }

    @Test
    void rechazaUidDesconocido() {
        when(clienteRepository.findByUidNfc("0000000000")).thenReturn(Optional.empty());

        assertThrows(ResourcesNotFoundException.class,
                () -> clienteService.findByUidNfc("0000000000"));
    }

    @Test
    void rechazaUidVacio() {
        assertThrows(UidNfcInvalidoException.class,
                () -> clienteService.findByUidNfc(" "));
    }
}
