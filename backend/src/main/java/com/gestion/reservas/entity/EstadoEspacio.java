package com.gestion.reservas.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "estados_espacio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoEspacio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEstado;

    private String descripcion;
}

