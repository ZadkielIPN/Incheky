package org.handel.incheki.mappers;

import org.handel.incheki.dtos.request.VentaRequest;
import org.handel.incheki.dtos.response.VentaResponse;
import org.handel.incheki.model.DetalleVenta;
import org.handel.incheki.model.Venta;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(
        componentModel = "spring",
        uses = {EntityReferenceMapper.class, DetalleVentaMapper.class}
)
public interface VentaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cajero", source = "cajeroId", qualifiedByName = "toCajero")
    @Mapping(target = "cliente", source = "clienteId", qualifiedByName = "toCliente")
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "descuento", ignore = true)
    @Mapping(target = "puntosGanados", ignore = true)
    Venta toEntityInternal(VentaRequest request);

    default Venta toEntity(VentaRequest request) {
        Venta entity = toEntityInternal(request);
        if (entity != null && entity.getDetalles() != null) {
            entity.getDetalles().forEach(detalle -> detalle.setVenta(entity));
        }
        return entity;
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cajero", source = "cajeroId", qualifiedByName = "toCajero")
    @Mapping(target = "cliente", source = "clienteId", qualifiedByName = "toCliente")
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "descuento", ignore = true)
    @Mapping(target = "puntosGanados", ignore = true)
    void updateInternal(VentaRequest request, @MappingTarget Venta venta);

    default void update(VentaRequest request, @MappingTarget Venta venta) {
        updateInternal(request, venta);
        if (request != null && request.getDetalles() != null && venta.getDetalles() != null) {
            venta.getDetalles().forEach(detalle -> detalle.setVenta(venta));
        }
    }

    @Mapping(target = "cajeroId", source = "cajero", qualifiedByName = "toCajeroId")
    @Mapping(target = "clienteId", source = "cliente", qualifiedByName = "toClienteId")
    @Mapping(target = "cajeroNombre", ignore = true)
    @Mapping(target = "clienteNombre", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "descuentoPromociones", ignore = true)
    @Mapping(target = "descuentoNivel", ignore = true)
    @Mapping(target = "descuentoPuntos", ignore = true)
    VentaResponse toResponseInternal(Venta entity);

    default VentaResponse toResponse(Venta entity) {
        VentaResponse response = toResponseInternal(entity);
        if (entity.getCajero() != null) {
            response.setCajeroNombre(formatName(entity.getCajero().getId(), entity.getCajero().getNombre(),
                    entity.getCajero().getApellido()));
        }
        if (entity.getCliente() != null) {
            response.setClienteNombre(formatName(entity.getCliente().getId(), entity.getCliente().getNombre(),
                    entity.getCliente().getApellido()));
        }
        calcularDesglose(entity, response);
        return response;
    }

    default void calcularDesglose(Venta entity, VentaResponse response) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal promociones = BigDecimal.ZERO;
        if (entity.getDetalles() != null) {
            for (DetalleVenta detalle : entity.getDetalles()) {
                if (detalle.getSubtotal() != null) subtotal = subtotal.add(detalle.getSubtotal());
                if (detalle.getDescuentoImporte() != null) promociones = promociones.add(detalle.getDescuentoImporte());
            }
        }
        int puntosUtilizados = entity.getPuntosUtilizados() != null ? entity.getPuntosUtilizados() : 0;
        BigDecimal descuentoPuntos = BigDecimal.valueOf(puntosUtilizados)
                .multiply(new BigDecimal("0.50"))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal combinado = entity.getDescuento() != null ? entity.getDescuento() : BigDecimal.ZERO;
        BigDecimal descuentoNivel = combinado.subtract(descuentoPuntos).max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);

        response.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
        response.setDescuentoPromociones(promociones.setScale(2, RoundingMode.HALF_UP));
        response.setDescuentoNivel(descuentoNivel);
        response.setDescuentoPuntos(descuentoPuntos);
    }

    default String formatName(Long id, String nombre, String apellido) {
        String fullName = (nombre + " " + apellido).trim();
        return id + " - " + fullName;
    }
}
