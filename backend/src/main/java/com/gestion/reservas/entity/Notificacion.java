package com.gestion.reservas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNotificacion;

    private String mensaje;

    private Boolean leida;

    private LocalDateTime fechaEnvio;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
}
