package org.handel.incheki.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Rol {
    CLIENTE("Cliente"),
    ADMINISTRADOR("Administrador"),
    CAJERO("Cajero"),;

    private final String descripcion;
}