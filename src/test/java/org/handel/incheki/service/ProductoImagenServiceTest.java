package org.handel.incheki.service;

import org.handel.incheki.dtos.request.ProductoRequest;
import org.handel.incheki.dtos.response.ProductoResponse;
import org.handel.incheki.mappers.ProductoMapper;
import org.handel.incheki.model.Producto;
import org.handel.incheki.repository.ProductoRepository;
import org.handel.incheki.service.impl.ProductoServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductoImagenServiceTest {

    @TempDir
    Path tempDir;

    @AfterEach
    void limpiarSincronizacion() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void reemplazaImagenYEliminaLaAnteriorSoloDespuesDelCommit() throws IOException {
        Path directorioImagenes = Files.createDirectories(tempDir.resolve("productos"));
        Path imagenAnterior = Files.writeString(directorioImagenes.resolve("anterior.jpg"), "anterior");
        Producto producto = mock(Producto.class);
        when(producto.getImagen()).thenReturn("anterior.jpg");
        ProductoRepository repository = mock(ProductoRepository.class);
        ProductoMapper mapper = mock(ProductoMapper.class);
        when(repository.findById(1L)).thenReturn(Optional.of(producto));
        when(repository.save(producto)).thenReturn(producto);
        when(mapper.toResponse(producto)).thenReturn(new ProductoResponse());
        ProductoServiceImpl service = newService(repository, mapper);

        TransactionSynchronizationManager.initSynchronization();
        service.update(1L, new ProductoRequest(),
                new MockMultipartFile("file", "nueva.jpg", "image/jpeg", "nueva".getBytes()));

        ArgumentCaptor<ProductoRequest> requestCaptor = ArgumentCaptor.forClass(ProductoRequest.class);
        verify(mapper).update(requestCaptor.capture(), eq(producto));
        String nuevaImagen = requestCaptor.getValue().getImagen();
        assertNotEquals("anterior.jpg", nuevaImagen);
        assertTrue(Files.exists(directorioImagenes.resolve(nuevaImagen)));
        assertTrue(Files.exists(imagenAnterior));

        for (TransactionSynchronization synchronization
                : TransactionSynchronizationManager.getSynchronizations()) {
            synchronization.afterCommit();
        }

        assertFalse(Files.exists(imagenAnterior));
        assertTrue(Files.exists(directorioImagenes.resolve(nuevaImagen)));
    }

    @Test
    void conservaImagenExistenteCuandoNoSeSubeUnArchivo() throws IOException {
        Path directorioImagenes = Files.createDirectories(tempDir.resolve("productos"));
        Path imagenActual = Files.writeString(directorioImagenes.resolve("actual.jpg"), "actual");
        Producto producto = mock(Producto.class);
        when(producto.getImagen()).thenReturn("actual.jpg");
        ProductoRepository repository = mock(ProductoRepository.class);
        ProductoMapper mapper = mock(ProductoMapper.class);
        when(repository.findById(1L)).thenReturn(Optional.of(producto));
        when(repository.save(producto)).thenReturn(producto);
        when(mapper.toResponse(producto)).thenReturn(new ProductoResponse());

        newService(repository, mapper).update(1L, new ProductoRequest(), null);

        assertTrue(Files.exists(imagenActual));
        verify(mapper).update(any(ProductoRequest.class), eq(producto));
    }

    private ProductoServiceImpl newService(ProductoRepository repository, ProductoMapper mapper) {
        ProductoServiceImpl service = new ProductoServiceImpl(repository, mapper);
        ReflectionTestUtils.setField(service, "uploadDir", tempDir.toString());
        return service;
    }
}
