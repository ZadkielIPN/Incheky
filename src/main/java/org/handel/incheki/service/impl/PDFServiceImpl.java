package org.handel.incheki.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.response.DetalleVentaResponse;
import org.handel.incheki.dtos.response.VentaResponse;
import org.handel.incheki.exceptions.ResourcesNotFoundException;
import org.handel.incheki.model.Cliente;
import org.handel.incheki.repository.ClienteRepository;
import org.handel.incheki.service.PDFService;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PDFServiceImpl implements PDFService {
    private final ClienteRepository clienteRepository;

    public byte[] generarTicketPdf(VentaResponse venta) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Cliente cliente = findClienteById(venta.getClienteId());

        Document document = new Document(PageSize.A5, 25, 25, 25, 25);

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            Font primaryFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, Color.DARK_GRAY);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.BLACK);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.DARK_GRAY);
            Font headerTableFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
            Font promoFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, new Color(21, 128, 61));

            Paragraph titulo = new Paragraph("INCHEKI STORE", primaryFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            Paragraph subtitulo = new Paragraph("Ticket de Venta #" + venta.getId(), subtitleFont);
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitulo);

            document.add(new Paragraph(" "));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String fechaFormateada = venta.getFecha() != null ? venta.getFecha().format(formatter) : "";

            String cajeroLimpio = venta.getCajeroNombre() != null
                    ? venta.getCajeroNombre().replaceAll("^\\d+\\s*-\\s*", "")
                    : "N/A";

            Paragraph info = new Paragraph();
            info.setFont(normalFont);
            info.add("Fecha: " + fechaFormateada + "\n");
            info.add("Cajero: " + cajeroLimpio + "\n");
            String clienteLimpio = venta.getClienteNombre() != null
                    ? venta.getClienteNombre().replaceAll("^\\d+\\s*-\\s*", "")
                    : "Público General";
            info.add("Cliente: " + clienteLimpio + "\n");
            info.add("Método de Pago: " + venta.getMetodoPago());
            document.add(info);

            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 4f, 1.5f, 1.5f});

            Color primaryColor = new Color(40, 40, 40);

            addCellHeader(table, "Cant", headerTableFont, primaryColor, Element.ALIGN_CENTER);
            addCellHeader(table, "Producto", headerTableFont, primaryColor, Element.ALIGN_LEFT);
            addCellHeader(table, "Precio Unitario", headerTableFont, primaryColor, Element.ALIGN_RIGHT);
            addCellHeader(table, "Subtotal", headerTableFont, primaryColor, Element.ALIGN_RIGHT);

            if (venta.getDetalles() != null) {
                for (DetalleVentaResponse detalle : venta.getDetalles()) {
                    String nombreProd = detalle.getProductoNombre() != null ? detalle.getProductoNombre() : "Producto #" + detalle.getProductoId();

                    addCell(table, String.valueOf(detalle.getCantidad()), normalFont, Element.ALIGN_CENTER, false);

                    addProductCell(table, nombreProd, detalle, normalFont, promoFont);

                    addCell(table, "$" + detalle.getPrecioUnitario(), normalFont, Element.ALIGN_RIGHT, false);
                    addCell(table, "$" + detalle.getSubtotal(), normalFont, Element.ALIGN_RIGHT, false);
                }
            }

            document.add(table);
            document.add(new Paragraph(" "));

            PdfPTable totalesTable = new PdfPTable(2);
            totalesTable.setWidthPercentage(100);
            totalesTable.setWidths(new float[]{6f, 2f});

            addTotalRow(totalesTable, "Subtotal:", money(venta.getSubtotal()), normalFont);
            addTotalRow(totalesTable, "Promociones aplicadas:", "-" + money(venta.getDescuentoPromociones()), normalFont);
            addTotalRow(totalesTable, "Descuento por nivel de lealtad (" + cliente.getNivelLealtad().toString() + "):",
                    "-" + money(venta.getDescuentoNivel()), normalFont);
            addTotalRow(totalesTable,
                    "Descuento por puntos (" + venta.getPuntosUtilizados() + "):",
                    "-" + money(venta.getDescuentoPuntos()), normalFont);
            addTotalRow(totalesTable, "TOTAL A PAGAR:", money(venta.getTotal()), boldFont);
            addTotalRow(totalesTable, "Puntos Ganados:", String.valueOf(venta.getPuntosGanados()), normalFont);

            document.add(totalesTable);

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

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

    private String money(BigDecimal value) {
        BigDecimal safe = value != null ? value : BigDecimal.ZERO;
        return "$" + safe.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private void addProductCell(PdfPTable table, String nombre, DetalleVentaResponse detalle, Font font, Font promoFont) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(5);
        cell.setBorderColor(new Color(220, 220, 220));
        cell.addElement(new Paragraph(nombre, font));

        BigDecimal descuento = detalle.getDescuentoImporte();
        if (descuento != null && descuento.signum() > 0) {
            BigDecimal pct = detalle.getDescuentoPorcentaje() != null ? detalle.getDescuentoPorcentaje() : BigDecimal.ZERO;
            cell.addElement(new Paragraph(
                    "Promo -" + pct.stripTrailingZeros().toPlainString() + "% (-" + money(descuento) + ")", promoFont));
        }
        table.addCell(cell);
    }

    private void addCell(PdfPTable table, String text, Font font, int alignment, boolean isBottomBorder) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(5);
        cell.setBorderColor(new Color(220, 220, 220));
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

    private Cliente findClienteById(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("Cliente con id " + id + " no encontrado"));
    }
}