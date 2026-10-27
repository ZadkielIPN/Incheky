package org.handel.incheki.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.handel.incheki.model.Proveedor;
import org.handel.incheki.repository.ProveedorRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ProveedorController {
    private final ProveedorRepository proveedorRepository;

    @GetMapping("/proveedores")
    public String proveedores(Model model) {
        model.addAttribute("proveedores", proveedorRepository.findAll(Sort.by("nombre")));
        model.addAttribute("proveedor", new Proveedor());
        return "proveedores";
    }

    @PostMapping("/proveedores")
    public String crearProveedor(@Valid @ModelAttribute("proveedor") Proveedor proveedor,
                                 BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Revisa los datos del proveedor.");
            return "redirect:/proveedores";
        }
        proveedorRepository.save(proveedor);
        redirectAttributes.addFlashAttribute("mensaje", "Proveedor registrado correctamente.");
        return "redirect:/proveedores";
    }
}
