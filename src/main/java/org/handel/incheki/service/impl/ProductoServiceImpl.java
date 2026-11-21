
package org.handel.incheki.service.impl;

import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.ProductoRequest;
import org.handel.incheki.dtos.response.ProductoResponse;
import org.handel.incheki.exceptions.ResourcesNotFoundException;
import org.handel.incheki.mappers.ProductoMapper;
import org.handel.incheki.model.Producto;
import org.handel.incheki.repository.ProductoRepository;
import org.handel.incheki.service.ProductoService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Override
    @Transactional
    public ProductoResponse create(ProductoRequest request, MultipartFile file) {
        if (request.getStock() != 0) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "El inventario inicial debe registrarse como abastecimiento para conservar su costo.");
        }
        if (file != null && !file.isEmpty()) {
            request.setImagen(saveImage(file));
        }

        Producto producto = productoMapper.toEntity(request);
        producto.setCostoPromedio(BigDecimal.ZERO.setScale(6));

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
            productosPage = productoRepository
                    .findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(
                            q, q, pageable
                    );
        } else {
            productosPage = productoRepository.findAll(pageable);
        }

        return productosPage.map(productoMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponse> findCatalog(String q, Pageable pageable) {
        Page<Producto> productosPage = productoRepository.findCatalog(
                q == null || q.isBlank() ? null : q.trim(),
                pageable
        );

        return productosPage.map(productoMapper::toResponse);
    }

    @Override
    @Transactional
    public ProductoResponse update(Long id, ProductoRequest request, MultipartFile file) {
        Producto producto = findProductoById(id);

        if (file != null && !file.isEmpty()) {
            request.setImagen(saveImage(file));
        }

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
                .orElseThrow(() ->
                        new ResourcesNotFoundException(
                                "Producto con id " + id + " no encontrado"
                        )
                );
    }

    private Path getImageDirectory() {
        return Paths.get(uploadDir, "productos")
                .toAbsolutePath()
                .normalize();
    }

    private String saveImage(MultipartFile archivo) {
        String tipo = archivo.getContentType();

        if (tipo == null || !tipo.startsWith("image/")) {
            throw new IllegalArgumentException(
                    "El archivo debe ser una imagen."
            );
        }

        try {
            Path carpetaImagenes = getImageDirectory();

            Files.createDirectories(carpetaImagenes);

            String original = StringUtils.cleanPath(
                    Objects.requireNonNull(archivo.getOriginalFilename())
            ).replaceAll("[^a-zA-Z0-9._-]", "_");

            String nombre = UUID.randomUUID() + "_" + original;

            Path destino = carpetaImagenes.resolve(nombre).normalize();

            if (!destino.startsWith(carpetaImagenes)) {
                throw new IllegalArgumentException(
                        "Nombre de archivo no válido."
                );
            }

            archivo.transferTo(destino);

            return nombre;

        } catch (IOException e) {
            throw new UncheckedIOException(
                    "No se pudo guardar la imagen", e
            );
        }
    }
}
