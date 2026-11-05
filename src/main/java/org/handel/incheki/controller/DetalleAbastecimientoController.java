package org.handel.incheki.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.DetalleAbastecimientoRequest;
import org.handel.incheki.dtos.response.DetalleAbastecimientoResponse;
import org.handel.incheki.service.DetalleAbastecimientoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/detalle-abastecimientos")
@RequiredArgsConstructor
public class DetalleAbastecimientoController {
    private final DetalleAbastecimientoService detalleDetalleAbastecimientoService;


    @GetMapping("/{id}")
    public ResponseEntity<DetalleAbastecimientoResponse> getDetalleAbastecimientoById(@PathVariable Long id) {
        DetalleAbastecimientoResponse response = detalleDetalleAbastecimientoService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<DetalleAbastecimientoResponse>> getAllDetalleAbastecimientos(Pageable pageable) {
        Page<DetalleAbastecimientoResponse> response = detalleDetalleAbastecimientoService.findAll(pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<DetalleAbastecimientoResponse> createDetalleAbastecimiento(@Valid @RequestBody DetalleAbastecimientoRequest request) {
        DetalleAbastecimientoResponse response = detalleDetalleAbastecimientoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetalleAbastecimientoResponse> updateDetalleAbastecimiento(@PathVariable Long id, @Valid @RequestBody DetalleAbastecimientoRequest request) {
        DetalleAbastecimientoResponse response = detalleDetalleAbastecimientoService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDetalleAbastecimiento(@PathVariable Long id) {
        detalleDetalleAbastecimientoService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
}
