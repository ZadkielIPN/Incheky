package org.handel.incheki.controller;

import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.response.ProductoResponse;

import org.handel.incheki.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DataController {
    private final ProductoService productoService;

    @GetMapping("/saludo")
    public String obtenerSaludo() {
        return "¡Hola desde Spring Boot!";
    }

    @GetMapping("productos/{id}")
    public ResponseEntity<ProductoResponse> obtenerProducto(@PathVariable Long id) {

        ProductoResponse producto = productoService.findById(id);

        return ResponseEntity.ok(producto);
    }
}
