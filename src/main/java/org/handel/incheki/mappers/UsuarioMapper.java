package org.handel.incheki.mappers;

import org.handel.incheki.dtos.UsuarioSession;
import org.handel.incheki.dtos.response.UsuarioResponse;
import org.handel.incheki.entities.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioResponse toResponse(Usuario entity);

    UsuarioSession toSession(Usuario usuario);
}
