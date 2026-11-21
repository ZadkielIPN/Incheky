package org.handel.incheki.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.GastoRequest;
import org.handel.incheki.dtos.response.GastoResponse;
import org.handel.incheki.service.impl.GastoServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gastos")
@RequiredArgsConstructor
public class GastoController {
    private final GastoServiceImpl gastoService;

    @PostMapping
    public ResponseEntity<GastoResponse> crear(@Valid @RequestBody GastoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gastoService.create(request));
    }
}
