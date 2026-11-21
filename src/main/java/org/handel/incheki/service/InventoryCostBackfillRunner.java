package org.handel.incheki.service;

import lombok.RequiredArgsConstructor;
import org.handel.incheki.repository.DetalleVentaRepository;
import org.handel.incheki.repository.ProductoRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryCostBackfillRunner implements ApplicationRunner {
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoRepository productoRepository;
    private final InventoryCostService inventoryCostService;

    @Override
    public void run(ApplicationArguments args) {
        if (detalleVentaRepository.existsByCostoUnitarioIsNull()
                || productoRepository.existsByCostoPromedioIsNull()) {
            inventoryCostService.rebuildCostsFromHistory();
        }
    }
}
