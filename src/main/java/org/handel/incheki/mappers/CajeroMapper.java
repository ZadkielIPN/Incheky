package org.handel.incheki.mappers;

import org.handel.incheki.dtos.request.CajeroRequest;
import org.handel.incheki.dtos.response.CajeroResponse;
import org.handel.incheki.entities.Cajero;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CajeroMapper {

    @Mapping(target = "id", ignore = true)
    @BeanMapping(builder = @Builder(disableBuilder = true))
    Cajero toEntity(CajeroRequest request);

    CajeroResponse toResponse(Cajero entity);
}
