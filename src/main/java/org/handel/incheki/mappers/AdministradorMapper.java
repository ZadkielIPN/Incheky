package org.handel.incheki.mappers;

import org.handel.incheki.dtos.request.AdministradorRequest;
import org.handel.incheki.dtos.response.AdministradorResponse;
import org.handel.incheki.entidades.Administrador;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdministradorMapper {

    @Mapping(target = "id", ignore = true)
    @BeanMapping(builder = @Builder(disableBuilder = true))
    Administrador toEntity(AdministradorRequest request);

    AdministradorResponse toResponse(Administrador entity);
}
