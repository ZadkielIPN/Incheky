package org.handel.incheki.mappers;

import org.handel.incheki.dtos.request.PromocionRequest;
import org.handel.incheki.dtos.response.PromocionResponse;
import org.handel.incheki.entities.Promocion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = EntityReferenceMapper.class)
public interface PromocionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productos", source = "productoIds", qualifiedByName = "toProductos")
    Promocion toEntity(PromocionRequest request);

    @Mapping(target = "productoIds", source = "productos", qualifiedByName = "toProductoIds")
    PromocionResponse toResponse(Promocion entity);
}
