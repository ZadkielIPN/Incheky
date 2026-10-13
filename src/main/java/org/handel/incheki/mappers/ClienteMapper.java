package org.handel.incheki.mappers;

import org.handel.incheki.dtos.request.ClienteRequest;
import org.handel.incheki.dtos.response.ClienteResponse;
import org.handel.incheki.entities.Cliente;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    @Mapping(target = "id", ignore = true)
    @BeanMapping(builder = @Builder(disableBuilder = true))
    Cliente toEntity(ClienteRequest request);

    ClienteResponse toResponse(Cliente entity);
}
