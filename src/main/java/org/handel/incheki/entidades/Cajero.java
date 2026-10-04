package org.handel.incheki.entidades;

import jakarta.persistence.*;
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

    @Column(nullable = false, name = "numero_empleado", unique = true)
    private Long numeroEmpleado;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Turno turno;

    @Column(name = "caja_asignada")
    private Long cajaAsignada;
}
