package org.handel.incheki.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.AbastecimientoRequest;
import org.handel.incheki.dtos.response.AbastecimientoResponse;
import org.handel.incheki.model.Abastecimiento;
import org.handel.incheki.repository.AbastecimientoRepository;
import org.handel.incheki.service.AbastecimientoService;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/abastecimientos")
@RequiredArgsConstructor
public class AbastecimientoController {
    private final AbastecimientoService abastecimientoService;
    private final AbastecimientoRepository abastecimientoRepository;

    @GetMapping("/abastecimientos")
    public String abastecimientos(Model model) {
        model.addAttribute("abastecimientos", abastecimientoRepository.findAll(Sort.by("nombre")));
        model.addAttribute("abastecimiento", new Abastecimiento());
        return "abastecimientos";
    }

    @PostMapping
    public String crearAbastecimiento(@Valid @ModelAttribute("abastecimiento") AbastecimientoRequest request,
                                 BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Revisa los datos del abastecimiento.");
            return "redirect:/abastecimientos";
        }
        abastecimientoService.create(request);
        redirectAttributes.addFlashAttribute("mensaje", "Abastecimiento registrado correctamente.");
        return "redirect:/abastecimientos";
    }

    @PostMapping("actualizar/{id}")
    public String update(@Valid @ModelAttribute("abastecimiento") AbastecimientoRequest request,
                         @PathVariable Long id,
                         BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Revisa los datos del abastecimiento.");
            return "redirect:/abastecimientos";
        }
        abastecimientoService.update(id, request);
        redirectAttributes.addFlashAttribute("mensaje", "Abastecimiento actualizado correctamente.");
        return "redirect:/abastecimientos";
    }

    @PostMapping("eliminar/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        abastecimientoService.delete(id);
        redirectAttributes.addFlashAttribute("mensaje", "Abastecimiento eliminado correctamente.");
        return "redirect:/abastecimientos";
    }
}
