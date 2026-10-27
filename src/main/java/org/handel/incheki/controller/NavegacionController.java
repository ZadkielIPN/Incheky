package org.handel.incheki.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.UsuarioSession;
import org.handel.incheki.dtos.request.ClienteRequest;
import org.handel.incheki.dtos.request.LoginRequest;
import org.handel.incheki.enums.Rol;
import org.handel.incheki.exceptions.ResourcesNotFoundException;
import org.handel.incheki.model.Usuario;
import org.handel.incheki.service.AdministradorService;
import org.handel.incheki.service.UsuarioService;
import org.handel.incheki.repository.ProductoRepository;
import org.handel.incheki.repository.ProveedorRepository;
import org.handel.incheki.repository.ClienteRepository;
import org.handel.incheki.model.Cliente;
import org.handel.incheki.enums.NivelLealtad;
import org.handel.incheki.model.Proveedor;
import org.handel.incheki.dtos.request.AdministradorRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class NavegacionController {
    private final UsuarioService usuarioService;
    private final AdministradorService administradorServicio;
    private final ProductoRepository productoRepository;

    @GetMapping("/")
    public String home(@RequestParam(required = false) String q,
                       Model model) {
        var productos = productoRepository.findAll(Sort.by("nombre"));
        if (q != null && !q.isBlank()) {
            String query = q.trim().toLowerCase();
            productos = productos.stream()
                    .filter(p -> p.getNombre().toLowerCase().contains(query)
                            || p.getDescripcion().toLowerCase().contains(query))
                    .toList();
        }
        model.addAttribute("productos", productos);
        model.addAttribute("busqueda", q == null ? "" : q);
        return "index";
    }

    @GetMapping("/supermercado")
    public String supermercado(Model model) {
        model.addAttribute("productos", productoRepository.findAll(Sort.by("nombre")));
        return "supermercado";
    }

    @GetMapping("/perfil")
    public String mostrarPerfil(HttpSession session, Model model) {
        UsuarioSession usuario = (UsuarioSession) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        // Dependiendo del rol, rediriges a su vista correspondiente
        if (usuario.getRol() == Rol.CLIENTE) {
            //model.addAttribute("paciente", pacienteServicio.findById(usuario.getId()));
            return "paciente/perfil";
        } else if (usuario.getRol() == Rol.CAJERO) {
            //model.addAttribute("doctor", doctorServicio.findById(usuario.getId()));
            //model.addAttribute("horarios", doctorServicio.findHorarios(usuario.getId()));
            return "doctor/doctor";
        } else if (usuario.getRol() == Rol.ADMINISTRADOR) {
            model.addAttribute("administrador", administradorServicio.findById(usuario.getId()));
            return "administrador/perfil";
        }

        return "redirect:/";
    }

    // Muestra el formulario de login
    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        model.addAttribute("loginDto", new LoginRequest());
        return "login";
    }

    // Procesa el formulario de login
    @PostMapping("/login")
    public String procesarLogin(@Valid @ModelAttribute("loginDto") LoginRequest loginDto,
                                BindingResult result,
                                HttpSession session,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "login";
        }

        try {
            UsuarioSession usuario = usuarioService.login(loginDto.getEmail(), loginDto.getContrasena());
            session.setAttribute("usuarioLogueado", usuario); // crea una sesión
            redirectAttributes.addFlashAttribute("mensaje", "Bienvenido, " + usuario.getNombre());
            return "redirect:/";
        } catch (ResourcesNotFoundException e) {
            model.addAttribute("loginDto", loginDto);
            model.addAttribute("error", "Email o contraseña incorrectos");
            return "login";
        } catch (Exception e) {
            model.addAttribute("loginDto", loginDto);
            model.addAttribute("error", "Ha ocurrido un error inesperado");
            return "login";
        }
    }

    // Cierra sesión
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // destruye la sesión
        return "redirect:/";
    }
}