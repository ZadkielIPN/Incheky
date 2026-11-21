package org.handel.incheki.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.handel.incheki.enums.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReporteVentaResponse {
    private Long id;
    private LocalDateTime fecha;
    private String cliente;
    private MetodoPago metodoPago;
    private BigDecimal subtotal;
    private BigDecimal descuentos;
    private BigDecimal total;
    private BigDecimal costoMercancia;
    private BigDecimal utilidadBruta;
}
