package org.handel.incheki.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.response.DetalleVentaResponse;
import org.handel.incheki.dtos.response.VentaResponse;
import org.handel.incheki.service.PDFService;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PDFServiceImpl implements PDFService {

    public byte[] generarTicketPdf(VentaResponse venta) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Margenes compactos tipo ticket / recibo (A5)
        Document document = new Document(PageSize.A5, 25, 25, 25, 25);

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            // ----------------------------------------------------
            // FUENTES Y COLORES CORPORATIVOS
            // ----------------------------------------------------
            Font primaryFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, Color.DARK_GRAY);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.BLACK);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.DARK_GRAY);
            Font headerTableFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);

            // ----------------------------------------------------
            // 1. ENCABEZADO
            // ----------------------------------------------------
            Paragraph titulo = new Paragraph("INCHEKI STORE", primaryFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            Paragraph subtitulo = new Paragraph("Ticket de Venta #" + venta.getId(), subtitleFont);
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitulo);

            document.add(new Paragraph(" ")); // Espacio

            // ----------------------------------------------------
            // 2. DATOS GENERALES (Cajero limpio sin ID)
            // ----------------------------------------------------
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String fechaFormateada = venta.getFecha() != null ? venta.getFecha().format(formatter) : "";

            // Limpiamos el nombre del cajero por si trae formato tipo "6 - Juan Mercado"
            String cajeroLimpio = venta.getCajeroNombre() != null
                    ? venta.getCajeroNombre().replaceAll("^\\d+\\s*-\\s*", "")
                    : "N/A";

            Paragraph info = new Paragraph();
            info.setFont(normalFont);
            info.add("Fecha: " + fechaFormateada + "\n");
            info.add("Cajero: " + cajeroLimpio + "\n");
            info.add("Cliente: " + (venta.getClienteNombre() != null ? venta.getClienteNombre() : "Público General") + "\n");
            info.add("Método de Pago: " + venta.getMetodoPago());
            document.add(info);

            document.add(new Paragraph(" ")); // Espacio

            // ----------------------------------------------------
            // 3. TABLA DE PRODUCTOS (Con Nombre en vez de ID)
            // ----------------------------------------------------
            PdfPTable table = new PdfPTable(4); // 4 Columnas: Cant, Producto, P.Unit, Subtotal
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 4f, 1.5f, 1.5f}); // Proporción de columnas

            // Cabecera de la tabla con estilo elegante (Fondo oscuro, texto blanco)
            Color primaryColor = new Color(40, 40, 40);

            addCellHeader(table, "Cant", headerTableFont, primaryColor, Element.ALIGN_CENTER);
            addCellHeader(table, "Producto", headerTableFont, primaryColor, Element.ALIGN_LEFT);
            addCellHeader(table, "P.Unit", headerTableFont, primaryColor, Element.ALIGN_RIGHT);
            addCellHeader(table, "Subtotal", headerTableFont, primaryColor, Element.ALIGN_RIGHT);

            // Filas de productos
            if (venta.getDetalles() != null) {
                for (DetalleVentaResponse detalle : venta.getDetalles()) {
                    String nombreProd = detalle.getProductoNombre() != null ? detalle.getProductoNombre() : "Producto #" + detalle.getProductoId();

                    addCell(table, String.valueOf(detalle.getCantidad()), normalFont, Element.ALIGN_CENTER, false);
                    addCell(table, nombreProd, normalFont, Element.ALIGN_LEFT, false);
                    addCell(table, "$" + detalle.getPrecioUnitario(), normalFont, Element.ALIGN_RIGHT, false);
                    addCell(table, "$" + detalle.getSubtotal(), normalFont, Element.ALIGN_RIGHT, false);
                }
            }

            document.add(table);
            document.add(new Paragraph(" "));

            // ----------------------------------------------------
            // 4. TOTALES Y PUNTOS
            // ----------------------------------------------------
            PdfPTable totalesTable = new PdfPTable(2);
            totalesTable.setWidthPercentage(100);
            totalesTable.setWidths(new float[]{6f, 2f});

            addTotalRow(totalesTable, "Descuento por Puntos:", "-$" + venta.getDescuentoPorPuntos(), normalFont);
            addTotalRow(totalesTable, "Puntos Utilizados:", String.valueOf(venta.getPuntosUtilizados()), normalFont);
            addTotalRow(totalesTable, "Puntos Ganados:", String.valueOf(venta.getPuntosGanados()), normalFont);
            addTotalRow(totalesTable, "TOTAL A PAGAR:", "$" + venta.getTotal(), boldFont);

            document.add(totalesTable);

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // Pie de página
            Paragraph footer = new Paragraph("¡Gracias por su compra en Incheki!", subtitleFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error al generar el PDF del ticket", e);
        }

        return baos.toByteArray();
    }

    private void addCellHeader(PdfPTable table, String text, Font font, Color bgColor, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setBackgroundColor(bgColor);
        cell.setPadding(6);
        cell.setBorderWidth(0);
        table.addCell(cell);
    }

    private void addCell(PdfPTable table, String text, Font font, int alignment, boolean isBottomBorder) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(5);
        cell.setBorderColor(new Color(220, 220, 220)); // Bordes sutiles grises
        table.addCell(cell);
    }

    private void addTotalRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell cellLabel = new PdfPCell(new Phrase(label, font));
        cellLabel.setBorder(Rectangle.NO_BORDER);
        cellLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellLabel.setPadding(3);

        PdfPCell cellValue = new PdfPCell(new Phrase(value, font));
        cellValue.setBorder(Rectangle.NO_BORDER);
        cellValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellValue.setPadding(3);

        table.addCell(cellLabel);
        table.addCell(cellValue);
    }
}