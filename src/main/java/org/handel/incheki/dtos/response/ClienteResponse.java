package org.handel.incheki.dtos.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.handel.incheki.enums.NivelLealtad;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ClienteResponse extends UsuarioResponse {

    private Integer puntos;
    private NivelLealtad nivelLealtad;
}
