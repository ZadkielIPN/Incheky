package org.handel.incheki.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReporteAbastecimientoResponse {
    private Long id;
    private LocalDateTime fecha;
    private String proveedor;
    private BigDecimal total;
}
