package org.handel.incheki.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.handel.incheki.enums.MetodoPago;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VentaRequest {

    @NotNull
    private LocalDateTime fecha;

    @NotNull
    private Long cajeroId;

    private Long clienteId;

    @Valid
    private List<DetalleVentaRequest> detalles;

    @NotNull
    @Min(0)
    private Integer puntosUtilizados;

    @NotNull
    private MetodoPago metodoPago;
}
