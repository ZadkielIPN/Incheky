package org.handel.incheki.mappers;

import org.handel.incheki.dtos.request.ProveedorRequest;
import org.handel.incheki.dtos.response.ProveedorResponse;
import org.handel.incheki.entidades.Proveedor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProveedorMapper {

    @Mapping(target = "id", ignore = true)
    Proveedor toEntity(ProveedorRequest request);

    ProveedorResponse toResponse(Proveedor entity);
}
