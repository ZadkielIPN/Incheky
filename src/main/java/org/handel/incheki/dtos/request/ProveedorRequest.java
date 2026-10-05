package org.handel.incheki.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorRequest {

    @NotBlank
    @Size(max = 150)
    private String nombre;

    @Size(max = 20)
    private String telefono;

    @Email
    @Size(max = 150)
    private String email;

    @Size(max = 250)
    private String direccion;

    private boolean activo = true;
}
