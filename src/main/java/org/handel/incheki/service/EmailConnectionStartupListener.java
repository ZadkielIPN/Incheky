package org.handel.incheki.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailConnectionStartupListener {

    private final JavaMailSenderImpl mailSender;

    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void verifyEmailConnection() {
        try {
            mailSender.testConnection();
            log.info("Conexión con el servidor de correo verificada.");
        } catch (Exception exception) {
            log.warn("No se pudo verificar la conexión con el servidor de correo al iniciar. "
                    + "El envío de correo puede reintentarse más tarde.", exception);
        }
    }
}
