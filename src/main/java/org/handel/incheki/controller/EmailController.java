package org.handel.incheki.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.handel.incheki.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
@Validated
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/ticket/{ventaId}")
    public ResponseEntity<String> enviarTicketPorCorreo(
            @PathVariable Long ventaId,
            @RequestParam @NotBlank @Email String emailDestino) {

        emailService.enviarTicketPorCorreo(ventaId, emailDestino);

        return ResponseEntity.accepted()
                .body("¡Petición en proceso! El ticket de la venta #" + ventaId + " se está enviando a " + emailDestino);
    }
}