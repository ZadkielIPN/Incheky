package org.handel.incheki.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.handel.incheki.enums.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VentaResponse {

    private Long id;
    private LocalDateTime fecha;
    private Long cajeroId;
    private Long clienteId;
    private List<DetalleVentaResponse> detalles;
    private BigDecimal total;
    private BigDecimal descuentoPorPuntos;
    private Integer puntosUtilizados;
    private Integer puntosGanados;
    private MetodoPago metodoPago;
}
