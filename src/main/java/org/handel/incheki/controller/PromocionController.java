package org.handel.incheki.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.PromocionRequest;
import org.handel.incheki.model.Promocion;
import org.handel.incheki.repository.PromocionRepository;
import org.handel.incheki.service.PromocionService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class PromocionController {
    private final PromocionService promocionService;
    private final PromocionRepository promocionRepository;

    @GetMapping("/promociones")
    public String showAll(Model model) {
        model.addAttribute("promociones", promocionRepository.findAll(Sort.by("nombre")));
        model.addAttribute("promocion", new Promocion());
        return "promociones";
    }

    @PostMapping("/promociones")
    public String create(@Valid @ModelAttribute("promocion") PromocionRequest request,
                                 BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Revisa los datos del promocion.");
            return "redirect:/promociones";
        }
        promocionService.create(request);
        redirectAttributes.addFlashAttribute("mensaje", "Promocion registrado correctamente.");
        return "redirect:/promociones";
    }

    @PostMapping("/promociones/{id}")
    public String update(@Valid @ModelAttribute("promocion") PromocionRequest request,
                                 @PathVariable Long id,
                                 BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Revisa los datos del promocion.");
            return "redirect:/promociones";
        }
        promocionService.update(id, request);
        redirectAttributes.addFlashAttribute("mensaje", "Promocion actualizado correctamente.");
        return "redirect:/promociones";
    }

    @PostMapping("/promociones/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        promocionService.delete(id);
        redirectAttributes.addFlashAttribute("mensaje", "Promocion eliminado correctamente.");
        return "redirect:/promociones";
    }
}
