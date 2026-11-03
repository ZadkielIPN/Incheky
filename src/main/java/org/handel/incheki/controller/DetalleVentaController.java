package org.handel.incheki.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.DetalleVentaRequest;
import org.handel.incheki.model.DetalleVenta;
import org.handel.incheki.repository.DetalleVentaRepository;
import org.handel.incheki.service.DetalleVentaService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/detalle-ventas")
@RequiredArgsConstructor
public class DetalleVentaController {
    private final DetalleVentaService detalleVentaService;
    private final DetalleVentaRepository detalleVentaRepository;

    @GetMapping
    public String detalleVentas(Model model) {
        model.addAttribute("detalleVentas", detalleVentaRepository.findAll(Sort.by("nombre")));
        model.addAttribute("detalleVenta", new DetalleVenta());
        return "detalleVentas";
    }

    @PostMapping
    public String crearDetalleVenta(@Valid @ModelAttribute("detalleVenta") DetalleVentaRequest request,
                                 BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Revisa los datos del detalleVenta.");
            return "redirect:/detalleVentas";
        }
        detalleVentaService.create(request);
        redirectAttributes.addFlashAttribute("mensaje", "Detalle Venta registrado correctamente.");
        return "redirect:/detalleVentas";
    }

    @PostMapping("actualizar/{id}")
    public String update(@Valid @ModelAttribute("detalleVenta") DetalleVentaRequest request,
                         @PathVariable Long id,
                         BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Revisa los datos del detalleVenta.");
            return "redirect:/detalleVentas";
        }
        detalleVentaService.update(id, request);
        redirectAttributes.addFlashAttribute("mensaje", "Detalle Venta actualizado correctamente.");
        return "redirect:/detalleVentas";
    }

    @PostMapping("eliminar/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        detalleVentaService.delete(id);
        redirectAttributes.addFlashAttribute("mensaje", "Detalle Venta eliminado correctamente.");
        return "redirect:/detalleVentas";
    }
}
