package org.handel.incheki.service.impl;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.response.VentaResponse;
import org.handel.incheki.service.EmailService;
import org.handel.incheki.service.PDFService;
import org.handel.incheki.service.VentaService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final PDFService pdfService;
    private final VentaService ventaService;

    @Async
    public CompletableFuture<Void> enviarTicketPorCorreo(Long ventaId, String correoDestino) {
        try {
            VentaResponse venta = ventaService.findById(ventaId);

            byte[] pdfBytes = pdfService.generarTicketPdf(venta);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom("incheky@gmail.com");
            helper.setTo(correoDestino);
            helper.setSubject("Ticket de Compra #" + venta.getId() + " - Incheki Store");

            String cuerpoHtml = "<h3>¡Gracias por tu compra en Incheki Store!</h3>" +
                    "<p>Hola <b>" + (venta.getClienteNombre() != null ? venta.getClienteNombre() : "Cliente") + "</b>,</p>" +
                    "<p>Adjunto encontrarás el ticket digital de tu compra con ID <b>#" + venta.getId() + "</b>.</p>" +
                    "<br><p><i>Atentamente,<br>Equipo de Incheki</i></p>";

            helper.setText(cuerpoHtml, true);

            helper.addAttachment("ticket_venta_" + venta.getId() + ".pdf", new ByteArrayResource(pdfBytes));

            mailSender.send(mimeMessage);

            System.out.println("[Email Asíncrono] ¡Ticket enviado con éxito a: " + correoDestino + "!");

        } catch (Exception e) {
            System.err.println("[Error] No se pudo enviar el ticket por correo: " + e.getMessage());
        }

        return CompletableFuture.completedFuture(null);
    }
}
