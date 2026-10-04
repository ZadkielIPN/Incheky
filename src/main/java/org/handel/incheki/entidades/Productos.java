package org.handel.incheki.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.handel.incheki.enums.UnidadMedida;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Table(name = "productos")
public class Productos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    @DecimalMin(value = "0.00", message = "El precio no puede ser negativo")
    private BigDecimal precio;

    @Column(nullable = false)
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(nullable = false, unique = true, name = "codigo_barras")
    private String codigoBarras;

    @Column(nullable = false, name = "unidad_medida")
    @Enumerated(EnumType.STRING)
    private UnidadMedida unidadMedida;

    @Column(nullable = false, name = "fecha_caducidad")
    private LocalDate fechaCaducidad;
}
