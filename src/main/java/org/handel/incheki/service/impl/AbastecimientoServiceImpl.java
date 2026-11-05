package org.handel.incheki.service.impl;

import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.request.AbastecimientoRequest;
import org.handel.incheki.dtos.response.AbastecimientoResponse;
import org.handel.incheki.exceptions.ResourcesNotFoundException;
import org.handel.incheki.mappers.AbastecimientoMapper;
import org.handel.incheki.model.Abastecimiento;
import org.handel.incheki.model.DetalleAbastecimiento;
import org.handel.incheki.model.Producto;
import org.handel.incheki.repository.AbastecimientoRepository;
import org.handel.incheki.repository.ProductoRepository;
import org.handel.incheki.repository.ProveedorRepository;
import org.handel.incheki.service.AbastecimientoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AbastecimientoServiceImpl implements AbastecimientoService {
    private final AbastecimientoRepository abastecimientoRepository;
    private final AbastecimientoMapper abastecimientoMapper;
    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;

    @Override
    @Transactional
    public AbastecimientoResponse create(AbastecimientoRequest request) {
        validateDetails(request);
        Abastecimiento abastecimiento = abastecimientoMapper.toEntity(request);
        abastecimiento.setProveedor(proveedorRepository.findById(request.getProveedorId())
                .orElseThrow(() -> new ResourcesNotFoundException(
                        "Proveedor con id " + request.getProveedorId() + " no encontrado")));

        applyDetailsAndInventory(abastecimiento);
        abastecimientoRepository.save(abastecimiento);

        return abastecimientoMapper.toResponse(abastecimiento);
    }

    @Override
    @Transactional(readOnly = true)
    public AbastecimientoResponse findById(Long id) {
        Abastecimiento abastecimiento = findAbastecimientoById(id);
        return abastecimientoMapper.toResponse(abastecimiento);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AbastecimientoResponse> findAll(Pageable pageable) {
        return abastecimientoRepository.findAll(pageable)
                .map(abastecimientoMapper::toResponse);
    }

    @Override
    @Transactional
    public AbastecimientoResponse update(Long id, AbastecimientoRequest request) {
        validateDetails(request);
        Abastecimiento abastecimiento = findAbastecimientoById(id);

        restoreInventory(abastecimiento);
        abastecimientoMapper.update(request, abastecimiento);
        abastecimiento.setProveedor(proveedorRepository.findById(request.getProveedorId())
                .orElseThrow(() -> new ResourcesNotFoundException(
                        "Proveedor con id " + request.getProveedorId() + " no encontrado")));
        applyDetailsAndInventory(abastecimiento);

        return abastecimientoMapper.toResponse(abastecimiento);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Abastecimiento abastecimiento = findAbastecimientoById(id);
        restoreInventory(abastecimiento);
        abastecimientoRepository.delete(abastecimiento);
    }

    private void validateDetails(AbastecimientoRequest request) {
        if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("El abastecimiento debe incluir al menos un producto.");
        }
        Set<Long> productIds = new HashSet<>();
        request.getDetalles().forEach(detail -> {
            if (!productIds.add(detail.getProductoId())) {
                throw new IllegalArgumentException("No puedes agregar el mismo producto más de una vez.");
            }
        });
    }

    private void applyDetailsAndInventory(Abastecimiento abastecimiento) {
        BigDecimal total = BigDecimal.ZERO;
        for (DetalleAbastecimiento detalle : abastecimiento.getDetalles()) {
            Producto producto = productoRepository.findByIdForUpdate(detalle.getProducto().getId())
                    .orElseThrow(() -> new ResourcesNotFoundException(
                            "Producto con id " + detalle.getProducto().getId() + " no encontrado"));
            BigDecimal subtotal = detalle.getCostoUnitario()
                    .multiply(BigDecimal.valueOf(detalle.getCantidad()))
                    .setScale(2, RoundingMode.HALF_UP);

            producto.setStock(producto.getStock() + detalle.getCantidad());
            detalle.setProducto(producto);
            detalle.setAbastecimiento(abastecimiento);
            detalle.setSubtotal(subtotal);
            total = total.add(subtotal);
        }
        abastecimiento.setTotal(total.setScale(2, RoundingMode.HALF_UP));
    }

    private void restoreInventory(Abastecimiento abastecimiento) {
        for (DetalleAbastecimiento detalle : abastecimiento.getDetalles()) {
            Producto producto = productoRepository.findByIdForUpdate(detalle.getProducto().getId())
                    .orElseThrow(() -> new ResourcesNotFoundException(
                            "Producto con id " + detalle.getProducto().getId() + " no encontrado"));
            int restoredStock = producto.getStock() - detalle.getCantidad();
            if (restoredStock < 0) {
                throw new IllegalArgumentException(
                        "No se puede modificar o eliminar el abastecimiento porque el stock actual ya fue utilizado.");
            }
            producto.setStock(restoredStock);
        }
    }

    private Abastecimiento findAbastecimientoById(Long id) {
        return abastecimientoRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("Abastecimiento con id " + id + " no encontrado"));
    }
}
