package org.handel.incheki.service;

import org.handel.incheki.dtos.response.CajeroResponse;
import org.handel.incheki.exceptions.ResourcesNotFoundException;
import org.handel.incheki.exceptions.UidNfcInvalidoException;
import org.handel.incheki.mappers.CajeroMapper;
import org.handel.incheki.model.Cajero;
import org.handel.incheki.repository.CajeroRepository;
import org.handel.incheki.repository.UsuarioRepository;
import org.handel.incheki.service.impl.CajeroServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CajeroNfcServiceTest {

    private CajeroRepository cajeroRepository;
    private CajeroMapper cajeroMapper;
    private CajeroServiceImpl cajeroService;

    @BeforeEach
    void setUp() {
        cajeroRepository = mock(CajeroRepository.class);
        cajeroMapper = mock(CajeroMapper.class);
        cajeroService = new CajeroServiceImpl(cajeroRepository, cajeroMapper, mock(UsuarioRepository.class));
    }

    @Test
    void buscaCajeroPorUidConCerosIzquierda() {
        Cajero cajero = mock(Cajero.class);
        CajeroResponse response = new CajeroResponse();
        response.setUidNfc("0009580011");

        when(cajeroRepository.findByUidNfc("0009580011")).thenReturn(Optional.of(cajero));
        when(cajeroMapper.toResponse(cajero)).thenReturn(response);

        assertEquals(response, cajeroService.findByUidNfc(" 0009580011 "));
    }

    @Test
    void rechazaUidDesconocido() {
        when(cajeroRepository.findByUidNfc("0000000000")).thenReturn(Optional.empty());

        assertThrows(ResourcesNotFoundException.class,
                () -> cajeroService.findByUidNfc("0000000000"));
    }

    @Test
    void rechazaUidVacio() {
        assertThrows(UidNfcInvalidoException.class,
                () -> cajeroService.findByUidNfc(" "));
    }
}
