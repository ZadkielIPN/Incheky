package org.handel.incheki.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.AbastecimientoRequest;
import org.handel.incheki.dtos.response.AbastecimientoResponse;
import org.handel.incheki.service.AbastecimientoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/abastecimientos")
@RequiredArgsConstructor
public class AbastecimientoController {
    private final AbastecimientoService abastecimientoService;

    @GetMapping("/{id}")
    public ResponseEntity<AbastecimientoResponse> getAbastecimientoById(@PathVariable Long id) {
        AbastecimientoResponse response = abastecimientoService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<AbastecimientoResponse>> getAllAbastecimientos(Pageable pageable) {
        Page<AbastecimientoResponse> response = abastecimientoService.findAll(pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<AbastecimientoResponse> createAbastecimiento(@Valid @RequestBody AbastecimientoRequest request) {
        AbastecimientoResponse response = abastecimientoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AbastecimientoResponse> updateAbastecimiento(@PathVariable Long id, @Valid @RequestBody AbastecimientoRequest request) {
        AbastecimientoResponse response = abastecimientoService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAbastecimiento(@PathVariable Long id) {
        abastecimientoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
