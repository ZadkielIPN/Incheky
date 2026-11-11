package org.handel.incheki.service;

import org.handel.incheki.dtos.response.VentaResponse;

public interface PDFService {
    byte[] generarTicketPdf(VentaResponse venta);
}
