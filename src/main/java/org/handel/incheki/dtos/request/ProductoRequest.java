package org.handel.incheki.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.handel.incheki.enums.UnidadMedida;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRequest {

    @NotBlank
    @Size(max = 255)
    private String nombre;

    @NotBlank
    @Size(max = 255)
    private String descripcion;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal precio;

    @NotNull
    @Min(0)
    private Integer stock;

    private boolean activo = true;

    @NotBlank
    @Size(max = 50)
    private String codigoBarras;

    @NotNull
    private UnidadMedida unidadMedida;

    private LocalDate fechaCaducidad;

    private Set<Long> promocionIds;
}
