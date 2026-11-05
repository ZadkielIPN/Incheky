package org.handel.incheki.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.handel.incheki.enums.Turno;

@Getter
@Setter
@NoArgsConstructor
public class CajeroCreateRequest {

    @NotBlank
    @Size(max = 255)
    private String nombre;

    @NotBlank
    @Size(max = 255)
    private String apellido;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 20)
    private String contrasena;

    @NotNull
    @Min(1)
    private Long numeroEmpleado;

    @NotNull
    private Turno turno;

    @NotNull
    @Min(1)
    private Long cajaAsignada;
}
