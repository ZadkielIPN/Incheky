package org.handel.incheki.mappers;

import org.handel.incheki.dtos.request.DetalleAbastecimientoRequest;
import org.handel.incheki.dtos.response.DetalleAbastecimientoResponse;
import org.handel.incheki.model.DetalleAbastecimiento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = EntityReferenceMapper.class)
public interface DetalleAbastecimientoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "abastecimiento", ignore = true)
    @Mapping(target = "producto", source = "productoId", qualifiedByName = "toProducto")
    @Mapping(target = "subtotal", ignore = true)
    DetalleAbastecimiento toEntity(DetalleAbastecimientoRequest request);

    @Mapping(target = "productoId", source = "producto", qualifiedByName = "toProductoId")
    DetalleAbastecimientoResponse toResponse(DetalleAbastecimiento entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "abastecimiento", ignore = true)
    @Mapping(target = "producto", source = "productoId", qualifiedByName = "toProducto")
    @Mapping(target = "subtotal", ignore = true)
    void update(DetalleAbastecimientoRequest request, @MappingTarget DetalleAbastecimiento detalle);
}
