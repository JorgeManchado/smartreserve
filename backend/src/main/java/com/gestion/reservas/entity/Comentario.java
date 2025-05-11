package com.gestion.reservas.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comentarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comentario")
    private Long idComentario;

    private String texto;

    private Integer valoracion;

    private LocalDateTime fecha;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private EstadoComentario estado;

    @ManyToOne
    @JoinColumn(name = "id_reserva")
    private Reserva reserva;
}
