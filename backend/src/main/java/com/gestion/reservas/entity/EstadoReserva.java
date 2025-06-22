package com.gestion.reservas.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "estados_reserva")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoReserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEstado;

    private String descripcion;
    private String color;
    private String bgcolor;
}
