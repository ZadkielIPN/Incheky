package org.handel.incheki.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UnidadMedida {
    KILOGRAMO("Kilogramo"),
    GRAMO("Gramo"),
    LITRO("Litro"),
    MILILITRO("Mililitro"),
    METRO("Metro"),
    CENTIMETRO("Centímetro"),
    PIEZA("Pieza");

    private final String descripcion;
}
