package com.gestion.reservas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "reservas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReserva;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_espacio")
    private Espacio espacio;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private EstadoReserva estado;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Boolean sincronizado;
    private int ocupantes;
    private Boolean recomendadaia;
    private String eventid;
    private Boolean usadaenmodelo;
}

