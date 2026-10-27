package org.handel.incheki.mappers;

import org.handel.incheki.dtos.request.AdministradorRequest;
import org.handel.incheki.dtos.response.AdministradorResponse;
import org.handel.incheki.model.Administrador;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AdministradorMapper {

    @Mapping(target = "id", ignore = true)
    @BeanMapping(builder = @Builder(disableBuilder = true))
    Administrador toEntity(AdministradorRequest request);

    AdministradorResponse toResponse(Administrador entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "rol", ignore = true)
    void update(AdministradorRequest request, @MappingTarget Administrador administrador);
}
