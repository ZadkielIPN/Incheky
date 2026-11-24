package org.handel.incheki.config;

import jakarta.servlet.http.HttpServletResponse;
import org.handel.incheki.dtos.UsuarioSession;
import org.handel.incheki.enums.Rol;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApiAuthorizationInterceptorTest {

    @Test
    void permiteLoginNfcSinSesionPreexistente() {
        ApiAuthorizationInterceptor interceptor = new ApiAuthorizationInterceptor();
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login/nfc");
        HttpServletResponse response = new MockHttpServletResponse();

        assertTrue(interceptor.preHandle(request, response, new Object()));
    }

    @Test
    void soloAdministradorPuedeConsultarCajeroPorNfc() {
        ApiAuthorizationInterceptor interceptor = new ApiAuthorizationInterceptor();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/cajeros/tarjeta-nfc");
        request.setParameter("uidNfc", "0011522182");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuarioLogueado",
                new UsuarioSession(7L, "Cajero", "Uno", "cajero@example.com", null, Rol.CAJERO));
        request.setSession(session);

        assertThrows(ResponseStatusException.class,
                () -> interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }

    @Test
    void soloAdministradorPuedeAsignarUidNfc() {
        ApiAuthorizationInterceptor interceptor = new ApiAuthorizationInterceptor();
        MockHttpServletRequest request = new MockHttpServletRequest(
                "PATCH", "/api/usuarios/1/tarjeta-nfc");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuarioLogueado",
                new UsuarioSession(7L, "Cajero", "Uno", "cajero@example.com", null, Rol.CAJERO));
        request.setSession(session);

        assertThrows(ResponseStatusException.class,
                () -> interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }
}
