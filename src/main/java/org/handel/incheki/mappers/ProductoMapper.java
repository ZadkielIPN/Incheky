package org.handel.incheki.mappers;

import org.handel.incheki.dtos.request.ProductoRequest;
import org.handel.incheki.dtos.response.ProductoResponse;
import org.handel.incheki.model.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = EntityReferenceMapper.class)
public interface ProductoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "promociones", source = "promocionIds", qualifiedByName = "toPromociones")
    Producto toEntity(ProductoRequest request);

    @Mapping(target = "promocionIds", source = "promociones", qualifiedByName = "toPromocionIds")
    ProductoResponse toResponse(Producto entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "promociones", source = "promocionIds", qualifiedByName = "toPromociones")
    void update(ProductoRequest request, @MappingTarget Producto producto);
}
