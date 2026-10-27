package org.handel.incheki.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.AdministradorRequest;
import org.handel.incheki.dtos.response.AdministradorResponse;
import org.handel.incheki.exceptions.EmailDuplicadoException;
import org.handel.incheki.service.AdministradorService;
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
    final AdministradorService cajeroService;

    @GetMapping("/{id}")
    public String mostrarPorId(@PathVariable Long id, Model modelo) {
        AdministradorResponse respuestaDto = cajeroService.findById(id);
        modelo.addAttribute("cajero", respuestaDto);
        return "cajero/cajero";
    }

    @GetMapping
    public String mostrarTodos(@PageableDefault(size = 15, sort = "apellido") Pageable pageable, Model model) {
        Page<AdministradorResponse> cajeroes = cajeroService.findAll(pageable);
        model.addAttribute("cajeroes", cajeroes);
        return "cajero/cajeros";
    }

    @GetMapping("/crear")
    public String mostrarCrear(Model modelo) {
        modelo.addAttribute("cajero", new AdministradorRequest());
        return "cajero/crearCajero";
    }

    @PostMapping
    public String procesarCrear(@Valid @ModelAttribute("cajero") AdministradorRequest request,
                        BindingResult result,
                        RedirectAttributes redirectAttributes,
                        Model modelo) {

        if (result.hasErrors()) {
            return "cajero/crearCajero";
        }

        try {
            cajeroService.create(request);
            redirectAttributes.addFlashAttribute("mensaje", "Cajero guardado exitosamente");
            return "redirect:/";
        } catch (EmailDuplicadoException e) {
            modelo.addAttribute("error", e.getMessage());
            return "cajero/crearCajero";
        }
    }

    @GetMapping("/actualizar/{id}")
    public String mostrarActualizar(@PathVariable Long id, Model modelo) {
        AdministradorResponse cajero = cajeroService.findById(id);
        modelo.addAttribute("cajero", cajero);
        return "cajero/actualizarCajero";
    }

    @PostMapping("actualizar/{id}")
    public String procesarActualizar(@PathVariable Long id,
                         @Valid @ModelAttribute("cajero") AdministradorRequest solicitudDto,
                         BindingResult result,
                         Model modelo,
                         RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "cajero/actualizarCajero";
        }

        cajeroService.update(id, solicitudDto);
        redirectAttributes.addFlashAttribute("mensaje", "Cajero actualizado");
        return "redirect:/cajeros/" + id;
    }

    @PostMapping("eliminar/{id}")
    public String procesarEliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        cajeroService.delete(id);
        redirectAttributes.addFlashAttribute("mensaje", "Cajero eliminado");
        return "redirect:/";
    }
}
