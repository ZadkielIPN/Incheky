package org.handel.incheki.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.AdministradorCreateRequest;
import org.handel.incheki.dtos.request.AdministradorUpdateRequest;
import org.handel.incheki.dtos.response.AdministradorResponse;
import org.handel.incheki.service.AdministradorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/administradores")
@RequiredArgsConstructor
public class AdministradorController {
    private final AdministradorService administradorService;


    @GetMapping("/{id}")
    public ResponseEntity<AdministradorResponse> getAbastecimientoById(@PathVariable Long id) {
        AdministradorResponse response = administradorService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<AdministradorResponse>> getAllAbastecimientos(Pageable pageable) {
        Page<AdministradorResponse> response = administradorService.findAll(pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<AdministradorResponse> createAbastecimiento(@Valid @RequestBody AdministradorCreateRequest request) {
        AdministradorResponse response = administradorService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdministradorResponse> updateAbastecimiento(@PathVariable Long id, @Valid @RequestBody AdministradorUpdateRequest request) {
        AdministradorResponse response = administradorService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAbastecimiento(@PathVariable Long id) {
        administradorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
