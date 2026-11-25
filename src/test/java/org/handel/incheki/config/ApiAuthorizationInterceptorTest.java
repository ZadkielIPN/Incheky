package org.handel.incheki.config;

import jakarta.servlet.http.HttpServletResponse;
import org.handel.incheki.dtos.UsuarioSession;
import org.handel.incheki.enums.Rol;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void cajeroPuedeConsultarClientes() {
        assertDoesNotThrow(() -> preHandle("GET", "/api/clientes", Rol.CAJERO));
    }

    @Test
    void cajeroPuedeRegistrarVentas() {
        assertDoesNotThrow(() -> preHandle("POST", "/api/ventas", Rol.CAJERO));
    }

    @Test
    void soloAdministradorPuedeCrearClientes() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> preHandle("POST", "/api/clientes", Rol.CAJERO));

        assertTrue(exception.getStatusCode().isSameCodeAs(HttpStatus.FORBIDDEN));
        assertDoesNotThrow(() -> preHandle("POST", "/api/clientes", Rol.ADMINISTRADOR));
    }

    @Test
    void cajeroNoPuedeConsultarReporteContable() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> preHandle("GET", "/api/reportes/contabilidad", Rol.CAJERO));

        assertTrue(exception.getStatusCode().isSameCodeAs(HttpStatus.FORBIDDEN));
    }

    @Test
    void soloAdministradorPuedeEjecutarAjusteDeCostosDePrueba() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> preHandle("POST", "/api/reportes/ajustar-costos-datos-prueba-margen", Rol.CAJERO));

        assertTrue(exception.getStatusCode().isSameCodeAs(HttpStatus.FORBIDDEN));
        assertDoesNotThrow(() ->
                preHandle("POST", "/api/reportes/ajustar-costos-datos-prueba-margen", Rol.ADMINISTRADOR));
    }

    @Test
    void deniegaRutasApiSinPoliticaExplicita() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> preHandle("GET", "/api/ruta-no-clasificada", Rol.ADMINISTRADOR));

        assertTrue(exception.getStatusCode().isSameCodeAs(HttpStatus.FORBIDDEN));
    }

    private boolean preHandle(String method, String path, Rol role) throws Exception {
        ApiAuthorizationInterceptor interceptor = new ApiAuthorizationInterceptor();
        MockHttpServletRequest request = new MockHttpServletRequest(method, path);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuarioLogueado",
                new UsuarioSession(7L, "Usuario", "Prueba", "usuario@example.com", null, role));
        request.setSession(session);

        return interceptor.preHandle(request, new MockHttpServletResponse(), new Object());
    }
}
