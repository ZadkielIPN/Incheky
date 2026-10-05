package org.handel.incheki.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoMovimiento {
    COMPRA("Compra"),
    VENTA("Venta"),
    DEVOLUCION("Devolución"),
    AJUSTE("Ajuste"),
    CADUCADO("Caducado");

    private final String descripcion;
}
