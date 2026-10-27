package org.handel.incheki.mappers;

import org.handel.incheki.dtos.request.ClienteRequest;
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
    @BeanMapping(builder = @Builder(disableBuilder = true))
    Cliente toEntity(ClienteRequest request);

    ClienteResponse toResponse(Cliente entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "rol", ignore = true)
    void update(ClienteRequest request, @MappingTarget Cliente cliente);
}
