package org.handel.incheki.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.AdministradorRequest;
import org.handel.incheki.dtos.response.AdministradorResponse;
import org.handel.incheki.exceptions.EmailDuplicadoException;
import org.handel.incheki.service.AdministradorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/administradores")
@RequiredArgsConstructor
public class AdministradorController {
    final AdministradorService administradorService;

    @GetMapping("/{id}")
    public String showById(@PathVariable Long id, Model modelo) {
        AdministradorResponse respuestaDto = administradorService.findById(id);
        modelo.addAttribute("administrador", respuestaDto);
        return "administrador/administrador";
    }

    @GetMapping
    public String showAll(@PageableDefault(size = 15, sort = "apellido") Pageable pageable, Model model) {
        Page<AdministradorResponse> administradores = administradorService.findAll(pageable);
        model.addAttribute("administradores", administradores);
        model.addAttribute("administrador", nuevoFormulario());
        return "admin";
    }

    @GetMapping("/create")
    public String showCreate(Model modelo) {
        modelo.addAttribute("administrador", nuevoFormulario());
        return "admin";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("administrador") AdministradorRequest request,
                        BindingResult result,
                        RedirectAttributes redirectAttributes,
                        Model modelo) {

        if (result.hasErrors()) {
            request.setNumeroAdministrador(administradorService.siguienteNumero());
            addAdministradores(modelo);
            return "admin";
        }

        try {
            administradorService.create(request);
            redirectAttributes.addFlashAttribute("mensaje", "Administrador guardado exitosamente");
            return "redirect:/administradores";
        } catch (EmailDuplicadoException e) {
            modelo.addAttribute("error", e.getMessage());
            addAdministradores(modelo);
            return "admin";
        }
    }

    private void addAdministradores(Model model) {
        model.addAttribute("administradores", administradorService.findAll(
                PageRequest.of(0, 15, Sort.by("apellido"))));
    }

    @GetMapping("/showUpdate/{id}")
    public String showUpdate(@PathVariable Long id, Model modelo) {
        AdministradorResponse administrador = administradorService.findById(id);
        modelo.addAttribute("administrador", administrador);
        return "administrador/actualizarAdministrador";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("administrador") AdministradorRequest solicitudDto,
                         BindingResult result,
                         Model modelo,
                         RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "administrador/actualizarAdministrador";
        }

        administradorService.update(id, solicitudDto);
        redirectAttributes.addFlashAttribute("mensaje", "Administrador actualizado");
        return "redirect:/administradores/" + id;
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        administradorService.delete(id);
        redirectAttributes.addFlashAttribute("mensaje", "Administrador eliminado");
        return "redirect:/";
    }

    private AdministradorRequest nuevoFormulario() {
        AdministradorRequest request = new AdministradorRequest();
        request.setNumeroAdministrador(administradorService.siguienteNumero());
        return request;
    }
}
