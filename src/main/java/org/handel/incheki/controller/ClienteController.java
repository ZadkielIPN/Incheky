package org.handel.incheki.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.ClienteRequest;
import org.handel.incheki.repository.ClienteRepository;
import org.handel.incheki.service.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ClienteController {
    private final ClienteRepository clienteRepository;
    private final ClienteService clienteService;

    @GetMapping("/create")
    public String showCreate(Model model) {
        model.addAttribute("clienteDto", new ClienteRequest());
        return "registro";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("clienteDto") ClienteRequest request,
                              BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) return "registro";

        clienteService.create(request);

        redirectAttributes.addFlashAttribute("mensaje", "Cuenta creada correctamente.");
        return "redirect:/login";
    }
}
