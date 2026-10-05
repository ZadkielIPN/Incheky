package org.handel.incheki.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Table(name = "ventas")
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime fecha;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cajero_id", nullable = false)
    private Cajero cajero;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToMany(
            mappedBy = "venta",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<DetalleVenta> detalles = new ArrayList<>();

    @NotNull
    @DecimalMin(value = "0.00")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @NotNull
    @DecimalMin(value = "0.00")
    @Column(nullable = false, name = "descuento_por_puntos", precision = 10, scale = 2)
    private BigDecimal descuentoPorPuntos = BigDecimal.ZERO;

    @NotNull
    @Min(0)
    @Column(nullable = false, name = "puntos_utilizados")
    private Integer puntosUtilizados = 0;

    @NotNull
    @Min(0)
    @Column(nullable = false, name = "puntos_ganados")
    private Integer puntosGanados = 0;
}
