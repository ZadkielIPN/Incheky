package org.handel.incheki.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AbastecimientoRequest {

    @NotNull
    private LocalDateTime fecha;

    @NotNull
    private Long proveedorId;

    @Valid
    private List<DetalleAbastecimientoRequest> detalles;
}
