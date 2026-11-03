package org.handel.incheki.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.CajeroRequest;
import org.handel.incheki.dtos.response.CajeroResponse;
import org.handel.incheki.exceptions.EmailDuplicadoException;
import org.handel.incheki.service.CajeroService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cajeros")
@RequiredArgsConstructor
public class CajeroController {
    final CajeroService cajeroService;

    @GetMapping("/{id}")
    public String showById(@PathVariable Long id, Model modelo) {
        CajeroResponse respuestaDto = cajeroService.findById(id);
        modelo.addAttribute("cajero", respuestaDto);
        return "cajero";
    }

    @GetMapping
    public String showAll(@PageableDefault(size = 15, sort = "apellido") Pageable pageable, Model model) {
        Page<CajeroResponse> cajeros = cajeroService.findAll(pageable);
        model.addAttribute("cajeros", cajeros);
        return "cajero/cajeros";
    }

    @GetMapping("/create")
    public String showCreate(Model modelo) {
        modelo.addAttribute("cajero", new CajeroRequest());
        return "cajero/createCajero";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("cajero") CajeroRequest request,
                        BindingResult result,
                        RedirectAttributes redirectAttributes,
                        Model modelo) {

        if (result.hasErrors()) {
            return "cajero/createCajero";
        }

        try {
            cajeroService.create(request);
            redirectAttributes.addFlashAttribute("mensaje", "Cajero guardado exitosamente");
            return "redirect:/";
        } catch (EmailDuplicadoException e) {
            modelo.addAttribute("error", e.getMessage());
            return "cajero/createCajero";
        }
    }

    @GetMapping("/update/{id}")
    public String showUpdate(@PathVariable Long id, Model modelo) {
        CajeroResponse cajero = cajeroService.findById(id);
        modelo.addAttribute("cajero", cajero);
        return "cajero/updateCajero";
    }

    @PostMapping("update/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("cajero") CajeroRequest solicitudDto,
                         BindingResult result,
                         Model modelo,
                         RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "cajero/updateCajero";
        }

        cajeroService.update(id, solicitudDto);
        redirectAttributes.addFlashAttribute("mensaje", "Cajero actualizado");
        return "redirect:/cajeros/" + id;
    }

    @PostMapping("eliminar/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        cajeroService.delete(id);
        redirectAttributes.addFlashAttribute("mensaje", "Cajero eliminado");
        return "redirect:/";
    }
}
