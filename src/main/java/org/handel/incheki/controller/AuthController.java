package org.handel.incheki.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.UsuarioSession;
import org.handel.incheki.dtos.request.LoginRequest;
import org.handel.incheki.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<UsuarioSession> procesarLogin(@Valid @RequestBody LoginRequest loginDto,
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
}