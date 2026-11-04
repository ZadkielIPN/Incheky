package org.handel.incheki.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.ClienteRequest;
import org.handel.incheki.dtos.response.ClienteResponse;
import org.handel.incheki.service.ClienteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {
    private final ClienteService clienteService;

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> getAbastecimientoById(@PathVariable Long id) {
        ClienteResponse response = clienteService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ClienteResponse>> getAllAbastecimientos(Pageable pageable) {
        Page<ClienteResponse> response = clienteService.findAll(pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> createAbastecimiento(@Valid @RequestBody ClienteRequest request) {
        ClienteResponse response = clienteService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> updateAbastecimiento(@PathVariable Long id, @Valid @RequestBody ClienteRequest request) {
        ClienteResponse response = clienteService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAbastecimiento(@PathVariable Long id) {
        clienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
