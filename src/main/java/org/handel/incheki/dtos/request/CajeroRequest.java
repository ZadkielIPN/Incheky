package org.handel.incheki.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.handel.incheki.enums.Turno;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CajeroRequest extends UsuarioRequest {

    @NotNull
    @Min(1)
    private Long numeroEmpleado;

    @NotNull
    private Turno turno;

    @NotNull
    @Min(1)
    private Long cajaAsignada;
}
