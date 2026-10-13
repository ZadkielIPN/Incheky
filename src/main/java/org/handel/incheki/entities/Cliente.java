package org.handel.incheki.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    @Min(value = 0, message = "Los puntos mínimos permitidos son 0")
    @Column(nullable = false)
    private Integer puntos;

    @NotNull
    @Column(nullable = false, name = "nivel_lealtad")
    @Enumerated(EnumType.STRING)
    private NivelLealtad nivelLealtad;
}
