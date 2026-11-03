package org.handel.incheki.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.VentaRequest;
import org.handel.incheki.model.Venta;
import org.handel.incheki.repository.VentaRepository;
import org.handel.incheki.service.VentaService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ventas")
@RequiredArgsConstructor
public class VentaController {
    private final VentaService ventaService;
    private final VentaRepository ventaRepository;

    @GetMapping
    public String ventas(Model model) {
        model.addAttribute("ventas", ventaRepository.findAll(Sort.by("nombre")));
        model.addAttribute("venta", new Venta());
        return "ventas";
    }

    @PostMapping
    public String crearVenta(@Valid @ModelAttribute("venta") VentaRequest request,
                                 BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Revisa los datos del venta.");
            return "redirect:/ventas";
        }
        ventaService.create(request);
        redirectAttributes.addFlashAttribute("mensaje", "Venta registrado correctamente.");
        return "redirect:/ventas";
    }

    @PostMapping("actualizar/{id}")
    public String update(@Valid @ModelAttribute("venta") VentaRequest request,
                         @PathVariable Long id,
                         BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Revisa los datos del venta.");
            return "redirect:/ventas";
        }
        ventaService.update(id, request);
        redirectAttributes.addFlashAttribute("mensaje", "Venta actualizado correctamente.");
        return "redirect:/ventas";
    }

    @PostMapping("eliminar/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        ventaService.delete(id);
        redirectAttributes.addFlashAttribute("mensaje", "Venta eliminado correctamente.");
        return "redirect:/ventas";
    }
}
