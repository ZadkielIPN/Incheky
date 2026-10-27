package org.handel.incheki.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.handel.incheki.enums.Rol;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequest {

    @NotBlank
    @Size(message = "El nombre no puede estar en blanco",max = 255)
    private String nombre;

    @NotBlank
    @Size(message = "El apellido no puede estar en blanco",max = 255)
    private String apellido;

    @NotBlank(message = "El correo electrónico no puede quedar en blanco")
    @Email(message = "El formato del correo electrónico no es válido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar en blanco")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres y un máximo de 20", max = 20)
    private String contrasena;

    @NotNull
    private Rol rol;
}
