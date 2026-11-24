package org.handel.incheki.service;

import org.handel.incheki.mappers.ProductoMapper;
import org.handel.incheki.repository.ProductoRepository;
import org.handel.incheki.service.impl.ProductoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoBusquedaServiceTest {
    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ProductoMapper productoMapper;

    @InjectMocks
    private ProductoServiceImpl productoService;

    @Test
    void buscaPorNombreDescripcionOCodigoDeBarras() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productoRepository
                .findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCaseOrCodigoBarrasContainingIgnoreCase(
                        "750103", "750103", "750103", pageable))
                .thenReturn(Page.empty(pageable));

        assertTrue(productoService.findAll(" 750103 ", pageable).isEmpty());

        verify(productoRepository)
                .findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCaseOrCodigoBarrasContainingIgnoreCase(
                        "750103", "750103", "750103", pageable);
    }
}
