package org.handel.incheki.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.UsuarioSession;
import org.handel.incheki.dtos.request.ClienteCreateRequest;
import org.handel.incheki.dtos.request.LoginRequest;
import org.handel.incheki.dtos.request.UsuarioProfileRequest;
import org.handel.incheki.dtos.response.UsuarioResponse;
import org.handel.incheki.service.ClienteService;
import org.handel.incheki.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UsuarioService usuarioService;
    private final ClienteService clienteService;

    @PostMapping("/login")
    public ResponseEntity<UsuarioSession> login(@Valid @RequestBody LoginRequest loginDto,
                                                        HttpSession session) {
        UsuarioSession usuario = usuarioService.login(loginDto.getEmail(), loginDto.getContrasena());

        session.setAttribute("usuarioLogueado", usuario);

        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioSession> register(
            @Valid @RequestBody ClienteCreateRequest request,
            HttpSession session) {
        clienteService.create(request);
        UsuarioSession usuario = usuarioService.login(request.getEmail(), request.getContrasena());
        session.setAttribute("usuarioLogueado", usuario);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> currentUser(HttpSession session) {
        UsuarioSession usuario = requireSession(session);
        return ResponseEntity.ok(usuarioService.findById(usuario.getId()));
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioResponse> updateCurrentUser(
            @Valid @RequestBody UsuarioProfileRequest request,
            HttpSession session) {
        UsuarioSession usuario = requireSession(session);
        UsuarioResponse response = usuarioService.updateProfile(usuario.getId(), request);
        session.setAttribute("usuarioLogueado", new UsuarioSession(
                response.getId(),
                response.getNombre(),
                response.getApellido(),
                response.getEmail(),
                null,
                response.getRol()
        ));
        return ResponseEntity.ok(response);
    }

    private UsuarioSession requireSession(HttpSession session) {
        Object value = session.getAttribute("usuarioLogueado");
        if (!(value instanceof UsuarioSession usuario)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED,
                    "Debes iniciar sesión");
        }
        return usuario;
    }
}