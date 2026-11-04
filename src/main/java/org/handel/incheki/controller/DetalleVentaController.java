package org.handel.incheki.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.DetalleVentaRequest;
import org.handel.incheki.dtos.response.DetalleVentaResponse;
import org.handel.incheki.model.DetalleVenta;
import org.handel.incheki.repository.DetalleVentaRepository;
import org.handel.incheki.service.DetalleVentaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("/api/detalle-ventas")
@RequiredArgsConstructor
public class DetalleVentaController {
    private final DetalleVentaService detalleVentaService;


    @GetMapping("/{id}")
    public ResponseEntity<DetalleVentaResponse> getDetalleVentaById(@PathVariable Long id) {
        DetalleVentaResponse response = detalleVentaService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<DetalleVentaResponse>> getAllDetalleVentas(Pageable pageable) {
        Page<DetalleVentaResponse> response = detalleVentaService.findAll(pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<DetalleVentaResponse> createDetalleVenta(@Valid @RequestBody DetalleVentaRequest request) {
        DetalleVentaResponse response = detalleVentaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetalleVentaResponse> updateDetalleVenta(@PathVariable Long id, @Valid @RequestBody DetalleVentaRequest request) {
        DetalleVentaResponse response = detalleVentaService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDetalleVenta(@PathVariable Long id) {
        detalleVentaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
