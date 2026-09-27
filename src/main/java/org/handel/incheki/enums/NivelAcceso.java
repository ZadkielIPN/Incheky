package org.handel.incheki.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NivelAcceso {
    SUCURSAL("Sucursal"),
    GENERAL("General");

    private final String descripcion;
}
