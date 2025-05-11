package com.gestion.reservas.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "estados_comentario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoComentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado")
    private Long idEstado;

    private String descripcion;
}
