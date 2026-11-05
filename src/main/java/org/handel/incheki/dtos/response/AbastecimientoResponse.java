package org.handel.incheki.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AbastecimientoResponse {

    private Long id;
    private LocalDateTime fecha;
    private Long proveedorId;
    private String proveedorNombre;
    private List<DetalleAbastecimientoResponse> detalles;
    private BigDecimal total;
}
