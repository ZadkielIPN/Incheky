package org.handel.incheki.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public enum NivelLealtad {
    BRONCE(0),
    PLATA(5),
    ORO(10);

    private final BigDecimal descuentoPorcentaje;

    NivelLealtad(int descuentoPorcentaje) {
        this.descuentoPorcentaje = BigDecimal.valueOf(descuentoPorcentaje);
    }

    public BigDecimal getDescuentoPorcentaje() {
        return descuentoPorcentaje;
    }
}
