package org.handel.incheki.controller;

import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.response.UsuarioResponse;
import org.handel.incheki.enums.Rol;
import org.handel.incheki.service.UsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> getUsuarioById(@PathVariable Long id) {
        UsuarioResponse response = usuarioService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<UsuarioResponse>> getAllUsuarios(@RequestParam(required = false) Rol rol,
                                                                @RequestParam(required = false) String q,
                                                                Pageable pageable) {
        Page<UsuarioResponse> response = usuarioService.findAll(rol, q, pageable);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
