package org.handel.incheki.dtos.request;

import jakarta.validation.constraints.*;
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


    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    private String nombre;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    private String descripcion;

    @NotNull
    @DecimalMin(value = "0.00", message = "El precio debe ser positivo")
    private BigDecimal precio;

    @NotNull
    @Min(value = 0, message = "El stock debe ser positivo")
    private Integer stock;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal peso;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5.0")
    private BigDecimal valoracion = new BigDecimal("0.0");

    private boolean activo = true;

    @NotBlank(message = "El código de barras no puede estar vacío")
    @Size(max = 50, message = "El código de barras no puede exceder 50 caracteres")
    private String codigoBarras;

    @NotNull(message = "La unidad de medida no puede ser nula")
    private UnidadMedida unidadMedida;

    @Future(message = "La fecha de caducidad debe ser una fecha futura")
    private LocalDate fechaCaducidad;

    private Set<Long> promocionIds;
}
