package org.handel.incheki.mappers;

import org.handel.incheki.dtos.request.ProveedorRequest;
import org.handel.incheki.dtos.response.ProveedorResponse;
import org.handel.incheki.model.Proveedor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProveedorMapper {

    @Mapping(target = "id", ignore = true)
    Proveedor toEntity(ProveedorRequest request);

    ProveedorResponse toResponse(Proveedor entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(ProveedorRequest request, @MappingTarget Proveedor proveedor);
}
