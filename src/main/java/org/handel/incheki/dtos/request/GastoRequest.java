package org.handel.incheki.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class GastoRequest {
    @NotNull
    private LocalDateTime fecha;

    @NotBlank
    @Size(max = 80)
    private String categoria;

    @NotBlank
    @Size(max = 255)
    private String descripcion;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal monto;
}
