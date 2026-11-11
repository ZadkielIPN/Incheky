package org.handel.incheki.controller;

import lombok.RequiredArgsConstructor;
import org.handel.incheki.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    // Endpoint exclusivo para enviar el ticket por correo en segundo plano
    @PostMapping("/ticket/{ventaId}")
    public ResponseEntity<String> enviarTicketPorCorreo(
            @PathVariable Long ventaId,
            @RequestParam String emailDestino) {

        // Dispara la tarea asíncrona sin bloquear la respuesta HTTP
        emailService.enviarTicketPorCorreo(ventaId, emailDestino);

        return ResponseEntity.ok("¡Petición en proceso! El ticket de la venta #" + ventaId + " se está enviando a " + emailDestino);
    }
}
