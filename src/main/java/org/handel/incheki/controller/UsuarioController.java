package org.handel.incheki.controller;

import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.response.UsuarioResponse;
import org.handel.incheki.enums.Rol;
import org.handel.incheki.service.UsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/{id}")
    public String mostrarPorId(@PathVariable Long id, Model modelo) {
        UsuarioResponse usuario = usuarioService.findById(id);
        modelo.addAttribute("usuario", usuario);
        return "usuario";
    }

    @GetMapping
    public String findAll(
            @RequestParam(required = false) Rol rol,
            @PageableDefault(size = 15, sort = "apellido") Pageable pageable,
            Model modelo) {
        Page<UsuarioResponse> usuarios = usuarioService.findAll(rol, pageable);
        modelo.addAttribute("usuarios", usuarios);

        return "usuarios";
    }

   /* @PutMapping("/{id}")
    public String delete(@PathVariable Long id,
                         RedirectAttributes redirectAttributes) {
        usuarioService.delete(id);

        redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado");
        return "redirect:/";
    }*/
}
