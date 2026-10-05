package org.handel.incheki.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.handel.incheki.enums.NivelLealtad;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ClienteRequest extends UsuarioRequest {

    @NotNull
    @Min(0)
    private Integer puntos;

    @NotNull
    private NivelLealtad nivelLealtad;
}
