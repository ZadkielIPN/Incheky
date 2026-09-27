package org.handel.incheki.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Turno {
    MATUTINO("Matutino"),
    VESPERTINO("Vespertino"),
    NOCTURNO("Nocturno"),;

    private final String descripcion;
}
