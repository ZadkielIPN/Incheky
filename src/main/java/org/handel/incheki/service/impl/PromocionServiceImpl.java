package org.handel.incheki.service.impl;

import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.PromocionRequest;
import org.handel.incheki.dtos.response.PromocionResponse;
import org.handel.incheki.exceptions.ResourcesNotFoundException;
import org.handel.incheki.mappers.PromocionMapper;
import org.handel.incheki.model.Promocion;
import org.handel.incheki.model.Producto;
import org.handel.incheki.repository.PromocionRepository;
import org.handel.incheki.repository.ProductoRepository;
import org.handel.incheki.service.PromocionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class PromocionServiceImpl implements PromocionService {
    private final PromocionRepository promocionRepository;
    private final PromocionMapper promocionMapper;
    private final ProductoRepository productoRepository;

    @Override
    @Transactional
    public PromocionResponse create(PromocionRequest request) {
        Promocion promocion = promocionMapper.toEntity(request);
        promocion.setProductos(new HashSet<>());
        promocionRepository.save(promocion);
        syncProducts(promocion, request.getProductoIds());
        return promocionMapper.toResponse(promocion);
    }

    @Override
    @Transactional(readOnly = true)
    public PromocionResponse findById(Long id) {
        Promocion promocion = findPromocionById(id);

        return promocionMapper.toResponse(promocion);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PromocionResponse> findAll(String query, Pageable pageable) {
        return (query == null || query.isBlank()
                ? promocionRepository.findAll(pageable)
                : promocionRepository.findByNombreContainingIgnoreCase(query, pageable))
                .map(promocionMapper::toResponse);
    }

    @Override
    @Transactional
    public PromocionResponse update(Long id, PromocionRequest request) {
        Promocion promocion = findPromocionById(id);
        promocion.getProductos().forEach(producto -> producto.getPromociones().remove(promocion));
        promocion.getProductos().clear();
        promocionMapper.update(request, promocion);
        promocion.setProductos(new HashSet<>());
        syncProducts(promocion, request.getProductoIds());
        return promocionMapper.toResponse(promocion);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Promocion promocion = findPromocionById(id);
        promocion.getProductos().forEach(producto -> producto.getPromociones().remove(promocion));
        promocion.getProductos().clear();
        promocionRepository.delete(promocion);
    }

    private Promocion findPromocionById(Long id) {
        return promocionRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("Promocion con id " + id + " no encontrada"));
    }

    private void syncProducts(Promocion promocion, java.util.Set<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return;
        }
        var products = productoRepository.findAllById(productIds);
        if (products.size() != productIds.size()) {
            throw new ResourcesNotFoundException("Uno o más productos de la promoción no existen.");
        }
        for (Producto producto : products) {
            producto.getPromociones().add(promocion);
            promocion.getProductos().add(producto);
        }
    }
}
