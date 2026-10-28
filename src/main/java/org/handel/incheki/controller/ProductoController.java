package org.handel.incheki.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.ProductoRequest;
import org.handel.incheki.dtos.response.ProductoResponse;
import org.handel.incheki.enums.UnidadMedida;
import org.handel.incheki.service.ProductoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {
    final ProductoService productoService;

    @GetMapping("/{id}")
    public String showById(@PathVariable Long id, Model modelo) {
        ProductoResponse respuestaDto = productoService.findById(id);
        modelo.addAttribute("producto", respuestaDto);
        return "producto/producto";
    }

    @GetMapping
    public String showAll(@PageableDefault(size = 15, sort = "nombre") Pageable pageable, Model model) {
        Page<ProductoResponse> productos = productoService.findAll(pageable);
        model.addAttribute("productos", productos);
        model.addAttribute("producto", new ProductoRequest());
        model.addAttribute("unidadesMedida", UnidadMedida.values());
        return "productos";
    }

    @GetMapping("/create")
    public String showCreate(Model modelo) {
        modelo.addAttribute("producto", new ProductoRequest());
        modelo.addAttribute("unidadesMedida", UnidadMedida.values());
        return "productos";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("producto") ProductoRequest request,
                        BindingResult result,
                        @RequestParam(value = "archivoImagen", required = false) MultipartFile file,
                        RedirectAttributes redirectAttributes,
                        Model modelo) {

        if (result.hasErrors()) {
            modelo.addAttribute("error", "Error al guardar el producto. Por favor, revise los campos.");
            modelo.addAttribute("unidadesMedida", UnidadMedida.values());
            return "productos";
        }

        try {
            productoService.create(request, file);
        } catch (IllegalArgumentException e) {
            modelo.addAttribute("error", e.getMessage());
            modelo.addAttribute("unidadesMedida", UnidadMedida.values());
            return "productos";
        }

        redirectAttributes.addFlashAttribute("mensaje", "Producto guardado exitosamente");
        return "redirect:/productos";
    }

    @GetMapping("/update/{id}")
    public String showUpdate(@PathVariable Long id, Model modelo) {
        ProductoResponse producto = productoService.findById(id);
        modelo.addAttribute("producto", producto);
        return "producto/actualizarProducto";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("producto") ProductoRequest solicitudDto,
                         BindingResult result,
                         RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "producto/actualizarProducto";
        }

        productoService.update(id, solicitudDto);
        redirectAttributes.addFlashAttribute("mensaje", "Producto actualizado");
        return "redirect:/productos/" + id;
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productoService.delete(id);
        redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado");
        return "redirect:/";
    }
}
