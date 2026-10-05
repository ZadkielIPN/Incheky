package org.handel.incheki.mappers;

import org.handel.incheki.dtos.request.VentaRequest;
import org.handel.incheki.dtos.response.VentaResponse;
import org.handel.incheki.entidades.Venta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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

    @Mapping(target = "cajeroId", source = "cajero", qualifiedByName = "toCajeroId")
    @Mapping(target = "clienteId", source = "cliente", qualifiedByName = "toClienteId")
    VentaResponse toResponse(Venta entity);
}
