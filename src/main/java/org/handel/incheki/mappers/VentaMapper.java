package org.handel.incheki.mappers;

import org.handel.incheki.dtos.request.VentaRequest;
import org.handel.incheki.dtos.response.VentaResponse;
import org.handel.incheki.model.Venta;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        uses = {EntityReferenceMapper.class, DetalleVentaMapper.class}
)
public interface VentaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cajero", source = "cajeroId", qualifiedByName = "toCajero")
    @Mapping(target = "cliente", source = "clienteId", qualifiedByName = "toCliente")
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "descuentoPorPuntos", ignore = true)
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
    @Mapping(target = "descuentoPorPuntos", ignore = true)
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
    VentaResponse toResponse(Venta entity);
}
