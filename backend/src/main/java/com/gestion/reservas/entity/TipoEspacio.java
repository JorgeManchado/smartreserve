package com.gestion.reservas.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipos_espacio")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TipoEspacio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTipoEspacio;

    private String descripcion;
}

