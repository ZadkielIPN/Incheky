package org.handel.incheki.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.VentaRequest;
import org.handel.incheki.dtos.UsuarioSession;
import org.handel.incheki.dtos.response.VentaResponse;
import org.handel.incheki.enums.Rol;
import org.handel.incheki.exceptions.ResourcesNotFoundException;
import org.handel.incheki.service.PDFService;
import org.handel.incheki.service.VentaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {
    private final VentaService ventaService;
    private final PDFService pdfService;

    @GetMapping("/{id}")
    public ResponseEntity<VentaResponse> getVentaById(@PathVariable Long id) {
        VentaResponse response = ventaService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<VentaResponse>> getAllVentas(Pageable pageable) {
        Page<VentaResponse> response = ventaService.findAll(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mis-compras")
    public ResponseEntity<Page<VentaResponse>> getMyPurchases(
            Pageable pageable,
            HttpSession session) {
        Object value = session.getAttribute("usuarioLogueado");
        if (!(value instanceof UsuarioSession usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (usuario.getRol() != Rol.CLIENTE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(ventaService.findByClienteId(usuario.getId(), pageable));
    }

    @PostMapping
    public ResponseEntity<VentaResponse> createVenta(
            @Valid @RequestBody VentaRequest request,
            HttpSession session) {
        Object value = session.getAttribute("usuarioLogueado");
        if (!(value instanceof UsuarioSession usuario)
                || (usuario.getRol() != Rol.CAJERO && usuario.getRol() != Rol.ADMINISTRADOR)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        VentaResponse response = ventaService.create(request, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VentaResponse> updateVenta(@PathVariable Long id, @Valid @RequestBody VentaRequest request) {
        throw new ResponseStatusException(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Las ventas registradas no se pueden editar.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenta(@PathVariable Long id) {
        throw new ResponseStatusException(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Las ventas registradas no se pueden eliminar.");
    }

    @GetMapping("/{id}/ticket")
    public ResponseEntity<byte[]> descargarTicketPdf(@PathVariable Long id, HttpSession session) {
        Object value = session.getAttribute("usuarioLogueado");
        if (!(value instanceof UsuarioSession usuario)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        VentaResponse venta = ventaService.findById(id);

        if (usuario.getRol() == Rol.CLIENTE && !usuario.getId().equals(venta.getClienteId())) {
            throw new ResourcesNotFoundException("Venta no encontrada.");
        }

        byte[] pdfBytes = pdfService.generarTicketPdf(venta);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "ticket_venta_" + id + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
