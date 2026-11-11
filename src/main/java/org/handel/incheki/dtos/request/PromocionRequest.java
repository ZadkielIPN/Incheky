package org.handel.incheki.dtos.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PromocionRequest {

    @NotBlank
    @Size(max = 100)
    private String nombre;

    @NotNull
    @DecimalMin("0.00")
    @DecimalMax("100.00")
    private BigDecimal porcentajeDescuento;

    @NotNull
    private LocalDate fechaInicio;

    @PastOrPresent( message = "La fecha de finalización no puede ser una fecha pasada")
    private LocalDate fechaFin;

    private boolean activa = true;

    private Set<Long> productoIds;
}
