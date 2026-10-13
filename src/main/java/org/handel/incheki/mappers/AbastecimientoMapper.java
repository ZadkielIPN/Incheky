package org.handel.incheki.mappers;

import org.handel.incheki.dtos.request.AbastecimientoRequest;
import org.handel.incheki.dtos.response.AbastecimientoResponse;
import org.handel.incheki.entities.Abastecimiento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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

    @Mapping(target = "proveedorId", source = "proveedor", qualifiedByName = "toProveedorId")
    AbastecimientoResponse toResponse(Abastecimiento entity);
}
