package org.handel.incheki.mappers;

import org.handel.incheki.dtos.request.CajeroRequest;
import org.handel.incheki.dtos.response.CajeroResponse;
import org.handel.incheki.model.Cajero;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CajeroMapper {

    @Mapping(target = "id", ignore = true)
    @BeanMapping(builder = @Builder(disableBuilder = true))
    Cajero toEntity(CajeroRequest request);

    CajeroResponse toResponse(Cajero entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "rol", ignore = true)
    void update(CajeroRequest request, @MappingTarget Cajero cajero);
}
