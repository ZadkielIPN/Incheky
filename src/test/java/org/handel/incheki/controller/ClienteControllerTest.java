package org.handel.incheki.controller;

import org.handel.incheki.dtos.request.ClienteCreateRequest;
import org.handel.incheki.dtos.response.ClienteResponse;
import org.handel.incheki.enums.NivelLealtad;
import org.handel.incheki.enums.Rol;
import org.handel.incheki.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {
    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClienteController clienteController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clienteController).build();
    }

    @Test
    void creaClienteYRespondeCreated() throws Exception {
        ClienteResponse response = new ClienteResponse();
        response.setId(12L);
        response.setNombre("Prueba");
        response.setApellido("Usuario");
        response.setEmail("test@example.com");
        response.setRol(Rol.CLIENTE);
        response.setPuntos(0);
        response.setNivelLealtad(NivelLealtad.BRONCE);
        when(clienteService.create(any(ClienteCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Prueba",
                                  "apellido": "Usuario",
                                  "email": "test@example.com",
                                  "contrasena": "TestPassword123",
                                  "puntos": 100,
                                  "nivelLealtad": "ORO"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.rol").value("CLIENTE"));

        verify(clienteService).create(any(ClienteCreateRequest.class));
    }
}
