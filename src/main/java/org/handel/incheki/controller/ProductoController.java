package org.handel.incheki.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.ProductoRequest;
import org.handel.incheki.dtos.response.ProductoResponse;
import org.handel.incheki.service.ProductoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {
    final ProductoService productoService;

    @GetMapping("/catalogo")
    public ResponseEntity<Page<ProductoResponse>> getCatalogo(
            @RequestParam(required = false) String q,
            Pageable pageable) {
        return ResponseEntity.ok(productoService.findCatalog(q, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> getProductoById(@PathVariable Long id) {
        ProductoResponse response = productoService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ProductoResponse>> getAllProductos(@RequestParam(required = false) String q,
                                                                  Pageable pageable) {
        Page<ProductoResponse> response = productoService.findAll(q, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> createProducto(@Valid @RequestPart("producto") ProductoRequest request,
                                                           @RequestPart(value = "file", required = false) MultipartFile file) {
        ProductoResponse response = productoService.create(request, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> updateProducto(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        ProductoResponse response = productoService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
