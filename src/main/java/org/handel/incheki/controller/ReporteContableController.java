package org.handel.incheki.controller;

import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.response.AjusteCostosDatosPruebaResponse;
import org.handel.incheki.dtos.response.ReporteContableResponse;
import org.handel.incheki.service.AjusteCostosDatosPruebaService;
import org.handel.incheki.service.ReporteContableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteContableController {
    private final ReporteContableService reporteContableService;
    private final AjusteCostosDatosPruebaService ajusteCostosDatosPruebaService;

    @GetMapping("/contabilidad")
    public ResponseEntity<ReporteContableResponse> consultar(
            @RequestParam int anio,
            @RequestParam int mes) {
        return ResponseEntity.ok(reporteContableService.consultar(anio, mes));
    }

    @PostMapping("/ajustar-costos-datos-prueba-margen")
    public ResponseEntity<AjusteCostosDatosPruebaResponse> ajustarCostosDatosPruebaMargen() {
        return ResponseEntity.ok(ajusteCostosDatosPruebaService.ajustar());
    }
}
