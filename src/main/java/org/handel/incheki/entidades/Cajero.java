package org.handel.incheki.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.handel.incheki.enums.Turno;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
@SuperBuilder
@Table(name = "cajeros")
@PrimaryKeyJoinColumn(name = "id")
public class Cajero extends Usuario {

    @NotNull
    @Column(nullable = false, name = "numero_empleado", unique = true)
    private Long numeroEmpleado;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Turno turno;

    @NotNull
    @Column(nullable = false, name = "caja_asignada")
    private Long cajaAsignada;
}
