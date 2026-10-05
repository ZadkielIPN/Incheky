package org.handel.incheki.dtos.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AdministradorResponse extends UsuarioResponse {

    private Long numeroAdministrador;
}
