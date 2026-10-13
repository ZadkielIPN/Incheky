package org.handel.incheki.mappers;

import org.handel.incheki.dtos.request.ProductoRequest;
import org.handel.incheki.dtos.response.ProductoResponse;
import org.handel.incheki.entities.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = EntityReferenceMapper.class)
public interface ProductoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "promociones", source = "promocionIds", qualifiedByName = "toPromociones")
    Producto toEntity(ProductoRequest request);

    @Mapping(target = "promocionIds", source = "promociones", qualifiedByName = "toPromocionIds")
    ProductoResponse toResponse(Producto entity);
}
