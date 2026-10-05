package org.handel.incheki.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleAbastecimientoResponse {

    private Long id;
    private Long productoId;
    private Integer cantidad;
    private BigDecimal costoUnitario;
    private BigDecimal subtotal;
}
