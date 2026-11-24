package org.handel.incheki.service;

import org.handel.incheki.repository.DetalleVentaRepository;
import org.handel.incheki.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.DefaultApplicationArguments;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryCostBackfillRunnerTest {
    @Mock
    private DetalleVentaRepository detalleVentaRepository;
    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private InventoryCostService inventoryCostService;

    private InventoryCostBackfillRunner runner;

    @BeforeEach
    void setUp() {
        runner = new InventoryCostBackfillRunner(detalleVentaRepository, productoRepository, inventoryCostService);
    }

    @Test
    void rebuildsCostsWhenMissingValuesExist() throws Exception {
        when(detalleVentaRepository.existsByCostoUnitarioIsNull()).thenReturn(true);

        runner.run(new DefaultApplicationArguments());

        verify(inventoryCostService).rebuildCostsFromHistory();
    }

    @Test
    void doesNotRebuildWhenAllCostsArePresent() throws Exception {
        when(detalleVentaRepository.existsByCostoUnitarioIsNull()).thenReturn(false);
        when(productoRepository.existsByCostoPromedioIsNull()).thenReturn(false);

        runner.run(new DefaultApplicationArguments());

        org.mockito.Mockito.verifyNoInteractions(inventoryCostService);
    }
}
