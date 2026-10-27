package org.handel.incheki.service;

import org.handel.incheki.dtos.UsuarioSession;
import org.handel.incheki.dtos.response.UsuarioResponse;
import org.handel.incheki.enums.Rol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioService {

    UsuarioResponse findById(Long id);

    Page<UsuarioResponse> findAll(Rol rol, Pageable paginable);

    UsuarioSession login(String email, String password);

    void delete(Long id);

}
