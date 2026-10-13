package org.handel.incheki.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.handel.incheki.enums.Rol;

@Data
@AllArgsConstructor
public class UsuarioSession {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String numeroTelefono;
    private Rol rol;
}
