package org.handel.incheki.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NivelLealtad {
    BRONCE("Bronce"),
    PLATA("Plata"),
    ORO("Oro"),
    DIAMANTE("Diamante");

    private final String descripcion;
}
