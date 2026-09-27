package org.handel.incheki.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.handel.incheki.enums.NivelLealtad;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
@SuperBuilder
@Table(name = "clientes")
@PrimaryKeyJoinColumn(name = "id")
public class Cliente extends Usuario {

    @Min(value = 0, message = "El precio mínimo permitido es 0")
    @Column(nullable = false)
    private Double puntos;

    @Column(nullable = false, name = "nivel_lealtad")
    @Enumerated(EnumType.STRING)
    private NivelLealtad nivelLealtad;
}
