package org.handel.incheki.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public enum NivelLealtad {
    BRONCE("Bronce", BigDecimal.ZERO),
    PLATA("Plata", BigDecimal.valueOf(5)),
    ORO("Oro", BigDecimal.TEN);

    private final String descripcion;
    private final BigDecimal descuentoPorcentaje;
}
