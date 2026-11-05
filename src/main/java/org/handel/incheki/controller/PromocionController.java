package org.handel.incheki.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.PromocionRequest;
import org.handel.incheki.dtos.response.PromocionResponse;
import org.handel.incheki.model.Promocion;
import org.handel.incheki.repository.PromocionRepository;
import org.handel.incheki.service.PromocionService;
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
@RequestMapping(("/api/promociones"))
@RequiredArgsConstructor
public class PromocionController {
    private final PromocionService promocionService;

    @GetMapping("/{id}")
    public ResponseEntity<PromocionResponse> getPromocionById(@PathVariable Long id) {
        PromocionResponse response = promocionService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<PromocionResponse>> getAllPromocions(
            @RequestParam(required = false) String q, Pageable pageable) {
        Page<PromocionResponse> response = promocionService.findAll(q, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<PromocionResponse> createPromocion(@Valid @RequestBody PromocionRequest request) {
        PromocionResponse response = promocionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromocionResponse> updatePromocion(@PathVariable Long id, @Valid @RequestBody PromocionRequest request) {
        PromocionResponse response = promocionService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromocion(@PathVariable Long id) {
        promocionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
