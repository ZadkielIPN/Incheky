package org.handel.incheki.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.handel.incheki.dtos.UsuarioSession;
import org.handel.incheki.enums.Rol;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class ApiAuthorizationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();
        if (!path.startsWith(request.getContextPath() + "/api/")) {
            return true;
        }

        String apiPath = path.substring(request.getContextPath().length());
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }
        if (isPublicAuthEndpoint(apiPath)) {
            return true;
        }

        Object value = request.getSession(false) == null
                ? null
                : request.getSession(false).getAttribute("usuarioLogueado");
        if (!(value instanceof UsuarioSession user)) {
            throw new ResponseStatusException(UNAUTHORIZED, "Debes iniciar sesión");
        }

        Set<Rol> allowedRoles = allowedRoles(apiPath, request.getMethod());
        if (!allowedRoles.isEmpty() && !allowedRoles.contains(user.getRol())) {
            throw new ResponseStatusException(FORBIDDEN, "No tienes permisos para realizar esta operación");
        }
        return true;
    }

    private boolean isPublicAuthEndpoint(String path) {
        return "/api/auth/login".equals(path)
                || "/api/auth/register".equals(path)
                || "/api/auth/logout".equals(path);
    }

    private Set<Rol> allowedRoles(String path, String method) {
        if (path.startsWith("/api/auth/")) {
            return Set.of(Rol.CLIENTE, Rol.CAJERO, Rol.ADMINISTRADOR);
        }
        if (path.startsWith("/api/promociones") && isRead(method)) {
            return Set.of(Rol.ADMINISTRADOR, Rol.CAJERO);
        }
        if (path.startsWith("/api/administradores")
                || path.startsWith("/api/cajeros")
                || path.startsWith("/api/usuarios")
                || path.startsWith("/api/promociones")) {
            return Set.of(Rol.ADMINISTRADOR);
        }
        if (path.startsWith("/api/clientes")
                || path.startsWith("/api/proveedores")
                || path.startsWith("/api/productos")
                || path.startsWith("/api/abastecimientos")
                || path.startsWith("/api/detalle-abastecimientos")
                || path.startsWith("/api/detalle-ventas")) {
            return isRead(method)
                    ? path.startsWith("/api/productos/catalogo")
                        ? Set.of(Rol.CLIENTE, Rol.ADMINISTRADOR, Rol.CAJERO)
                        : Set.of(Rol.ADMINISTRADOR, Rol.CAJERO)
                    : Set.of(Rol.ADMINISTRADOR);
        }
        if (path.startsWith("/api/ventas")) {
            if (path.startsWith("/api/ventas/mis-compras")) {
                return Set.of(Rol.CLIENTE);
            }
            return Set.of(Rol.ADMINISTRADOR, Rol.CAJERO);
        }
        return Set.of();
    }

    private boolean isRead(String method) {
        return HttpMethod.GET.matches(method) || HttpMethod.HEAD.matches(method);
    }
}
