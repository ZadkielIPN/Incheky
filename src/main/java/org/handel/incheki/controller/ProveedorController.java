package org.handel.incheki.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.ProveedorRequest;
import org.handel.incheki.model.Proveedor;
import org.handel.incheki.repository.ProveedorRepository;
import org.handel.incheki.service.ProveedorService;
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
public class ProveedorController {
    private final ProveedorService proveedorService;
    private final ProveedorRepository proveedorRepository;

    @GetMapping("/proveedores")
    public String proveedores(Model model) {
        model.addAttribute("proveedores", proveedorRepository.findAll(Sort.by("nombre")));
        model.addAttribute("proveedor", new Proveedor());
        return "proveedores";
    }

    @PostMapping("/proveedores")
    public String crearProveedor(@Valid @ModelAttribute("proveedor") ProveedorRequest request,
                                 BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Revisa los datos del proveedor.");
            return "redirect:/proveedores";
        }
        proveedorService.create(request);
        redirectAttributes.addFlashAttribute("mensaje", "Proveedor registrado correctamente.");
        return "redirect:/proveedores";
    }

    @PostMapping("/proveedores/{id}")
    public String update(@Valid @ModelAttribute("proveedor") ProveedorRequest request,
                         @PathVariable Long id,
                         BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Revisa los datos del proveedor.");
            return "redirect:/proveedores";
        }
        proveedorService.update(id, request);
        redirectAttributes.addFlashAttribute("mensaje", "Proveedor actualizado correctamente.");
        return "redirect:/proveedores";
    }

    @PostMapping("/proveedores/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        proveedorService.delete(id);
        redirectAttributes.addFlashAttribute("mensaje", "Proveedor eliminado correctamente.");
        return "redirect:/proveedores";
    }
}
