package org.handel.incheki.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.handel.incheki.enums.UnidadMedida;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Table(name = "productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nombre;

    @NotBlank
    @Column(nullable = false)
    private String descripcion;

    @NotNull
    @DecimalMin(value = "0.00")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @NotNull
    @Column(nullable = false)
    @Min(value = 0)
    private Integer stock;

    @Column(nullable = false)
    private boolean activo = true;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, unique = true, name = "codigo_barras", length = 50)
    private String codigoBarras;

    @NotNull
    @Column(nullable = false, name = "unidad_medida")
    @Enumerated(EnumType.STRING)
    private UnidadMedida unidadMedida;

    @Column(name = "fecha_caducidad")
    private LocalDate fechaCaducidad;

    @ManyToMany
    @JoinTable(
            name = "promociones_productos",
            joinColumns = @JoinColumn(name = "producto_id"),
            inverseJoinColumns = @JoinColumn(name = "promocion_id")
    )
    private Set<Promocion> promociones = new HashSet<>();
}
