package org.handel.incheki.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.handel.incheki.enums.NivelLealtad;

@Getter
@Setter
@NoArgsConstructor
public class ClienteUpdateRequest {

    @NotBlank
    @Size(max = 255)
    private String nombre;

    @NotBlank
    @Size(max = 255)
    private String apellido;

    @NotBlank
    @Email
    private String email;

    @Pattern(regexp = "^$|^.{8,20}$", message = "La nueva contraseña debe tener entre 8 y 20 caracteres")
    private String contrasena;

    @NotNull
    @Min(0)
    private Integer puntos;

    @NotNull
    private NivelLealtad nivelLealtad;
}
