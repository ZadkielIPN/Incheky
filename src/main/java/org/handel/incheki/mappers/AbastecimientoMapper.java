package org.handel.incheki.mappers;

import org.handel.incheki.dtos.request.AbastecimientoRequest;
import org.handel.incheki.dtos.response.AbastecimientoResponse;
import org.handel.incheki.model.Abastecimiento;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        uses = {EntityReferenceMapper.class, DetalleAbastecimientoMapper.class}
)
public interface AbastecimientoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "proveedor", source = "proveedorId", qualifiedByName = "toProveedor")
    @Mapping(target = "total", ignore = true)
    Abastecimiento toEntityInternal(AbastecimientoRequest request);

    default Abastecimiento toEntity(AbastecimientoRequest request) {
        Abastecimiento entity = toEntityInternal(request);
        if (entity != null && entity.getDetalles() != null) {
            entity.getDetalles().forEach(detalle -> detalle.setAbastecimiento(entity));
        }
        return entity;
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "proveedor", source = "proveedorId", qualifiedByName = "toProveedor")
    @Mapping(target = "total", ignore = true)
    void updateInternal(AbastecimientoRequest request, @MappingTarget Abastecimiento abastecimiento);

    default void update(AbastecimientoRequest request, @MappingTarget Abastecimiento abastecimiento) {
        updateInternal(request, abastecimiento);
        if (request != null && request.getDetalles() != null && abastecimiento.getDetalles() != null) {
            abastecimiento.getDetalles().forEach(detalle -> detalle.setAbastecimiento(abastecimiento));
        }
    }

    @Mapping(target = "proveedorId", source = "proveedor", qualifiedByName = "toProveedorId")
    @Mapping(target = "proveedorNombre", ignore = true)
    AbastecimientoResponse toResponseInternal(Abastecimiento entity);

    default AbastecimientoResponse toResponse(Abastecimiento entity) {
        AbastecimientoResponse response = toResponseInternal(entity);
        if (entity.getProveedor() != null) {
            response.setProveedorNombre(entity.getProveedor().getId() + " - " + entity.getProveedor().getNombre());
        }
        return response;
    }
}
