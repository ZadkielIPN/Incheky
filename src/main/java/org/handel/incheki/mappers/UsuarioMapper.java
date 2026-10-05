package org.handel.incheki.mappers;

import org.handel.incheki.dtos.response.UsuarioResponse;
import org.handel.incheki.entidades.Usuario;
import org.mapstruct.Mapper;

/**
 * Mapper for the common response fields of the abstract Usuario hierarchy.
 * Concrete request mappings are provided by the subtype mappers.
 */
@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioResponse toResponse(Usuario entity);
}
