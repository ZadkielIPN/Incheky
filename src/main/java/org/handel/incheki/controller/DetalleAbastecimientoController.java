package org.handel.incheki.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.DetalleAbastecimientoRequest;
import org.handel.incheki.model.DetalleAbastecimiento;
import org.handel.incheki.repository.DetalleAbastecimientoRepository;
import org.handel.incheki.service.DetalleAbastecimientoService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/detalle-abastecimiento")
@RequiredArgsConstructor
public class DetalleAbastecimientoController {
    private final DetalleAbastecimientoService detalleAbastecimientoService;
    private final DetalleAbastecimientoRepository detalleAbastecimientoRepository;

    @GetMapping
    public String detalleAbastecimientos(Model model) {
        model.addAttribute("detalleAbastecimientos", detalleAbastecimientoRepository.findAll(Sort.by("nombre")));
        model.addAttribute("detalleAbastecimiento", new DetalleAbastecimiento());
        return "detalleAbastecimientos";
    }

    @PostMapping
    public String crearDetalleAbastecimiento(@Valid @ModelAttribute("detalleAbastecimiento") DetalleAbastecimientoRequest request,
                                 BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Revisa los datos del detalleAbastecimiento.");
            return "redirect:/detalleAbastecimientos";
        }
        detalleAbastecimientoService.create(request);
        redirectAttributes.addFlashAttribute("mensaje", "Detalle Abastecimiento registrado correctamente.");
        return "redirect:/detalleAbastecimientos";
    }

    @PostMapping("actualizar/{id}")
    public String update(@Valid @ModelAttribute("detalleAbastecimiento") DetalleAbastecimientoRequest request,
                         @PathVariable Long id,
                         BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Revisa los datos del detalleAbastecimiento.");
            return "redirect:/detalleAbastecimientos";
        }
        detalleAbastecimientoService.update(id, request);
        redirectAttributes.addFlashAttribute("mensaje", "Detalle Abastecimiento actualizado correctamente.");
        return "redirect:/detalleAbastecimientos";
    }

    @PostMapping("eliminar/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        detalleAbastecimientoService.delete(id);
        redirectAttributes.addFlashAttribute("mensaje", "Detalle Abastecimiento eliminado correctamente.");
        return "redirect:/detalleAbastecimientos";
    }
}
