package org.handel.incheki.service.impl;

import lombok.RequiredArgsConstructor;
import org.handel.incheki.dtos.UsuarioSession;
import org.handel.incheki.dtos.request.DetalleVentaRequest;
import org.handel.incheki.dtos.request.VentaRequest;
import org.handel.incheki.dtos.response.VentaResponse;
import org.handel.incheki.enums.Rol;
import org.handel.incheki.exceptions.ResourcesNotFoundException;
import org.handel.incheki.mappers.VentaMapper;
import org.handel.incheki.model.*;
import org.handel.incheki.repository.CajeroRepository;
import org.handel.incheki.repository.ClienteRepository;
import org.handel.incheki.repository.ProductoRepository;
import org.handel.incheki.repository.VentaRepository;
import org.handel.incheki.service.VentaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {
    private final VentaRepository ventaRepository;
    private final VentaMapper ventaMapper;
    private final CajeroRepository cajeroRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;

    @Override
    @Transactional
    public VentaResponse create(VentaRequest request) {
        Venta venta = ventaMapper.toEntity(request);

        ventaRepository.save(venta);
        return ventaMapper.toResponse(venta);
    }

    @Override
    @Transactional
    public VentaResponse create(VentaRequest request, UsuarioSession sessionUser) {
        if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La venta debe incluir al menos un producto.");
        }
        if (request.getClienteId() == null) {
            throw new IllegalArgumentException("Debes seleccionar un cliente.");
        }

        Long cajeroId = sessionUser.getRol() == Rol.CAJERO ? sessionUser.getId() : request.getCajeroId();
        if (cajeroId == null) {
            throw new IllegalArgumentException("Debes seleccionar un cajero.");
        }

        var cajero = cajeroRepository.findById(cajeroId)
                .orElseThrow(() -> new ResourcesNotFoundException("Cajero no encontrado."));
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourcesNotFoundException("Cliente no encontrado."));

        if (new HashSet<>(request.getDetalles().stream().map(DetalleVentaRequest::getProductoId).toList()).size()
                != request.getDetalles().size()) {
            throw new IllegalArgumentException("No puedes agregar el mismo producto más de una vez.");
        }

        Venta venta = ventaMapper.toEntity(request);
        venta.setCajero(cajero);
        venta.setCliente(cliente);
        venta.setTotal(BigDecimal.ZERO);
        venta.setDescuentoPorPuntos(BigDecimal.ZERO);

        BigDecimal subtotalConPromociones = BigDecimal.ZERO;
        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = productoRepository.findByIdForUpdate(detalle.getProducto().getId())
                    .orElseThrow(() -> new ResourcesNotFoundException("Producto no encontrado."));
            if (!producto.isActivo()) {
                throw new IllegalArgumentException("El producto " + producto.getNombre() + " está inactivo.");
            }
            if (producto.getStock() < detalle.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para " + producto.getNombre() + ".");
            }

            BigDecimal subtotal = producto.getPrecio()
                    .multiply(BigDecimal.valueOf(detalle.getCantidad()));
            BigDecimal promo = producto.getPromociones().stream()
                    .filter(item -> item.isActiva()
                            && !item.getFechaInicio().isAfter(LocalDate.now())
                            && (item.getFechaFin() == null || !item.getFechaFin().isBefore(LocalDate.now())))
                    .map(Promocion::getPorcentajeDescuento)
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
            BigDecimal descuento = subtotal.multiply(promo)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal totalDetalle = subtotal.subtract(descuento).setScale(2, RoundingMode.HALF_UP);

            producto.setStock(producto.getStock() - detalle.getCantidad());
            detalle.setProducto(producto);
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(subtotal);
            detalle.setDescuentoPorcentaje(promo);
            detalle.setDescuentoImporte(descuento);
            detalle.setImpuestoImporte(BigDecimal.ZERO);
            detalle.setTotal(totalDetalle);
            detalle.setPromocion(producto.getPromociones().stream()
                    .filter(item -> item.isActiva()
                            && !item.getFechaInicio().isAfter(LocalDate.now())
                            && (item.getFechaFin() == null || !item.getFechaFin().isBefore(LocalDate.now())))
                    .max(Comparator.comparing(Promocion::getPorcentajeDescuento))
                    .orElse(null));
            subtotalConPromociones = subtotalConPromociones.add(totalDetalle);
        }

        BigDecimal descuentoLealtad = subtotalConPromociones
                .multiply(cliente.getNivelLealtad().getDescuentoPorcentaje())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        int puntosDisponibles = Math.max(cliente.getPuntos(), 0);
        int puntosSolicitados = Math.max(request.getPuntosUtilizados(), 0);
        BigDecimal total = subtotalConPromociones
                .subtract(descuentoLealtad)
                .setScale(2, RoundingMode.HALF_UP);
        int puntosPorSaldo = total
                .divide(BigDecimal.valueOf(0.50), 0, RoundingMode.FLOOR)
                .intValue();
        int puntosUtilizados = Math.min(puntosSolicitados, Math.min(puntosDisponibles, puntosPorSaldo));
        BigDecimal descuentoPuntos = BigDecimal.valueOf(puntosUtilizados)
                .multiply(BigDecimal.valueOf(0.50))
                .setScale(2, RoundingMode.HALF_UP);
        total = total
                .subtract(descuentoPuntos)
                .max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
        int puntosGanados = total.setScale(0, RoundingMode.FLOOR).intValue() / 10;

        venta.setPuntosUtilizados(puntosUtilizados);
        venta.setPuntosGanados(puntosGanados);
        venta.setDescuentoPorPuntos(descuentoPuntos.add(descuentoLealtad));
        venta.setTotal(total);
        cliente.setPuntos(puntosDisponibles - puntosUtilizados + puntosGanados);

        ventaRepository.save(venta);
        return ventaMapper.toResponse(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public VentaResponse findById(Long id) {
        Venta venta = findVentaById(id);

        return ventaMapper.toResponse(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaResponse> findAll(Pageable pageable) {
        return ventaRepository.findAll(pageable)
                .map(ventaMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaResponse> findByClienteId(Long clienteId, Pageable pageable) {
        return ventaRepository.findByClienteId(clienteId, pageable)
                .map(ventaMapper::toResponse);
    }

    @Override
    @Transactional
    public VentaResponse update(Long id, VentaRequest request) {
        Venta venta = findVentaById(id);

        ventaMapper.update(request, venta);

        ventaRepository.save(venta);
        return ventaMapper.toResponse(venta);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Venta venta = findVentaById(id);

        ventaRepository.delete(venta);
    }

    private Venta findVentaById(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("Venta con id " + id + " no encontrado"));
    }
}
