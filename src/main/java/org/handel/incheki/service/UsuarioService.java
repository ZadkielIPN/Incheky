package org.handel.incheki.service;

import org.handel.incheki.dtos.UsuarioSession;
import org.handel.incheki.dtos.request.UsuarioProfileRequest;
import org.handel.incheki.dtos.response.UsuarioResponse;
import org.handel.incheki.enums.Rol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioService {

    UsuarioResponse findById(Long id);

    Page<UsuarioResponse> findAll(Rol rol, String query, Pageable paginable);

    UsuarioSession login(String email, String password);

    UsuarioSession loginNfc(String uidNfc);

    UsuarioResponse asignarTarjetaNfc(Long id, String uidNfc);

    UsuarioResponse updateProfile(Long id, UsuarioProfileRequest request);

    void delete(Long id);

}
