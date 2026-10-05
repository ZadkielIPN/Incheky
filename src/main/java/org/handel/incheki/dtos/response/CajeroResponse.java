package org.handel.incheki.dtos.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.handel.incheki.enums.Turno;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CajeroResponse extends UsuarioResponse {

    private Long numeroEmpleado;
    private Turno turno;
    private Long cajaAsignada;
}
