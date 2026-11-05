package org.handel.incheki.mappers;

import org.handel.incheki.dtos.request.ClienteCreateRequest;
import org.handel.incheki.dtos.request.ClienteUpdateRequest;
import org.handel.incheki.dtos.response.ClienteResponse;
import org.handel.incheki.model.Cliente;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rol", ignore = true)
    @BeanMapping(builder = @Builder(disableBuilder = true))
    Cliente toEntity(ClienteCreateRequest request);

    ClienteResponse toResponse(Cliente entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rol", ignore = true)
    @Mapping(target = "contrasena", ignore = true)
    void update(ClienteUpdateRequest request, @MappingTarget Cliente cliente);
}
