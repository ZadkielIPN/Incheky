package org.handel.incheki.entidades;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.handel.incheki.enums.NivelAcceso;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
@SuperBuilder
@Table(name = "administradores")
@PrimaryKeyJoinColumn(name = "id")
public class Administrador extends Usuario {

    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, name = "numero_administrador", unique = true)
    private Long numeroAdministrador;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NivelAcceso nivelAcceso;
}

