package org.handel.incheki.service.impl;

import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.ProductoRequest;
import org.handel.incheki.dtos.response.ProductoResponse;
import org.handel.incheki.exceptions.ResourcesNotFoundException;
import org.handel.incheki.mappers.ProductoMapper;
import org.handel.incheki.model.Producto;
import org.handel.incheki.repository.ProductoRepository;
import org.handel.incheki.repository.UsuarioRepository;
import org.handel.incheki.service.ProductoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {
    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;
    private static final Path CARPETA_IMAGENES = Path.of("uploads", "productos");

    @Override
    @Transactional
    public ProductoResponse create(ProductoRequest request, MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            request.setImagen(saveImage(file));
        }

        Producto producto = productoMapper.toEntity(request);

        productoRepository.save(producto);
        return productoMapper.toResponse(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse findById(Long id) {
        Producto producto = findProductoById(id);

        return productoMapper.toResponse(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponse> findAll(Pageable pageable) {
        return productoRepository.findAll(pageable)
                .map(productoMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponse> findAll(String q, Pageable pageable) {
        Page<Producto> productosPage;

        if (q != null && !q.isBlank()) {
            productosPage = productoRepository.findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(
                    q, q, pageable
            );
        } else {
            productosPage = productoRepository.findAll(pageable);
        }

        return productosPage.map(productoMapper::toResponse);
    }

    @Override
    @Transactional
    public ProductoResponse update(Long id, ProductoRequest request) {
        Producto producto = findProductoById(id);

        productoMapper.update(request, producto);

        productoRepository.save(producto);
        return productoMapper.toResponse(producto);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Producto producto = findProductoById(id);

        productoRepository.delete(producto);
    }

    private Producto findProductoById(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("Producto con id " + id + " no encontrado"));
    }

    private String saveImage(MultipartFile archivo) {
        String tipo = archivo.getContentType();
        if (tipo == null || !tipo.startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen.");
        }
        try {
            Files.createDirectories(CARPETA_IMAGENES);
            String original = StringUtils.cleanPath(archivo.getOriginalFilename()).replaceAll("[^a-zA-Z0-9._-]", "_");
            String nombre = UUID.randomUUID() + "_" + original;
            archivo.transferTo(CARPETA_IMAGENES.resolve(nombre));
            return nombre;
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar la imagen", e);
        }
    }
}
