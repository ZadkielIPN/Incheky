package org.handel.incheki.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.CajeroRequest;
import org.handel.incheki.dtos.response.CajeroResponse;
import org.handel.incheki.service.CajeroService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cajeros")
@RequiredArgsConstructor
public class CajeroController {
    private final CajeroService cajeroService;


    @GetMapping("/{id}")
    public ResponseEntity<CajeroResponse> getAbastecimientoById(@PathVariable Long id) {
        CajeroResponse response = cajeroService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<CajeroResponse>> getAllAbastecimientos(Pageable pageable) {
        Page<CajeroResponse> response = cajeroService.findAll(pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CajeroResponse> createAbastecimiento(@Valid @RequestBody CajeroRequest request) {
        CajeroResponse response = cajeroService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CajeroResponse> updateAbastecimiento(@PathVariable Long id, @Valid @RequestBody CajeroRequest request) {
        CajeroResponse response = cajeroService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAbastecimiento(@PathVariable Long id) {
        cajeroService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
