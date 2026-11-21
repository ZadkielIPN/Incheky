package org.handel.incheki.mappers;

import org.handel.incheki.dtos.request.GastoRequest;
import org.handel.incheki.dtos.response.GastoResponse;
import org.handel.incheki.model.Gasto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface GastoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoria", source = "categoria", qualifiedByName = "trim")
    @Mapping(target = "descripcion", source = "descripcion", qualifiedByName = "trim")
    Gasto toEntity(GastoRequest request);

    GastoResponse toResponse(Gasto entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoria", source = "categoria", qualifiedByName = "trim")
    @Mapping(target = "descripcion", source = "descripcion", qualifiedByName = "trim")
    void update(GastoRequest request, @MappingTarget Gasto gasto);

    @Named("trim")
    default String trim(String value) {
        return value == null ? null : value.trim();
    }
}
