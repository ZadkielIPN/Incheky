package org.handel.incheki.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Table(
        name = "detalles_venta",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_detalle_venta_producto",
                        columnNames = {"venta_id", "producto_id"}
                )
        }
)
public class DetalleVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @ManyToOne(optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @NotNull
    @Min(value = 1)
    @Column(nullable = false)
    private Integer cantidad;

    @NotNull
    @DecimalMin(value = "0.00")
    @Column(nullable = false, name = "precio_unitario", precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @NotNull
    @DecimalMin(value = "0.00")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @ManyToOne
    @JoinColumn(name = "promocion_id")
    private Promocion promocion;

    @NotNull
    @DecimalMin(value = "0.00")
    @DecimalMax(value = "100.00")
    @Column(name = "descuento_porcentaje", nullable = false, precision = 5, scale = 2)
    private BigDecimal descuentoPorcentaje = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.00")
    @Column(nullable = false, name = "descuento_importe", precision = 10, scale = 2)
    private BigDecimal descuentoImporte = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.00")
    @Column(nullable = false, name = "impuesto_importe", precision = 10, scale = 2)
    private BigDecimal impuestoImporte = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.00")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
}