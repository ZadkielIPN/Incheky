package org.handel.incheki.mappers;

import org.handel.incheki.dtos.request.DetalleVentaRequest;
import org.handel.incheki.dtos.response.DetalleVentaResponse;
import org.handel.incheki.model.DetalleVenta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = EntityReferenceMapper.class)
public interface DetalleVentaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "venta", ignore = true)
    @Mapping(target = "producto", source = "productoId", qualifiedByName = "toProducto")
    @Mapping(target = "promocion", source = "promocionId", qualifiedByName = "toPromocion")
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "descuentoPorcentaje", ignore = true)
    @Mapping(target = "descuentoImporte", ignore = true)
    @Mapping(target = "impuestoImporte", ignore = true)
    @Mapping(target = "total", ignore = true)
    DetalleVenta toEntity(DetalleVentaRequest request);

    @Mapping(target = "productoId", source = "producto", qualifiedByName = "toProductoId")
    @Mapping(target = "promocionId", source = "promocion", qualifiedByName = "toPromocionId")
    DetalleVentaResponse toResponse(DetalleVenta entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "venta", ignore = true)
    @Mapping(target = "producto", source = "productoId", qualifiedByName = "toProducto")
    @Mapping(target = "promocion", source = "promocionId", qualifiedByName = "toPromocion")
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "descuentoPorcentaje", ignore = true)
    @Mapping(target = "descuentoImporte", ignore = true)
    @Mapping(target = "impuestoImporte", ignore = true)
    @Mapping(target = "total", ignore = true)
    void update(DetalleVentaRequest request, @MappingTarget DetalleVenta detalle);
}
