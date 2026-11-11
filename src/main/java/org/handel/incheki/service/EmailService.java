package org.handel.incheki.service;

import java.util.concurrent.CompletableFuture;

public interface EmailService {
    CompletableFuture<Void> enviarTicketPorCorreo(Long ventaId, String correoDestino);
}
