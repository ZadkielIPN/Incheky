package org.handel.incheki.mappers;

import org.handel.incheki.dtos.request.AdministradorCreateRequest;
import org.handel.incheki.dtos.request.AdministradorUpdateRequest;
import org.handel.incheki.dtos.response.AdministradorResponse;
import org.handel.incheki.model.Administrador;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AdministradorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "numeroAdministrador", ignore = true)
    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(target = "rol", ignore = true)
    Administrador toEntity(AdministradorCreateRequest request);

    AdministradorResponse toResponse(Administrador entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "rol", ignore = true)
    @Mapping(target = "numeroAdministrador", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contrasena", ignore = true)
    void update(AdministradorUpdateRequest request, @MappingTarget Administrador administrador);
}
