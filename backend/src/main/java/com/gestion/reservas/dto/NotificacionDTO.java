package com.gestion.reservas.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDTO {
    private Long idNotificacion;
    private String mensaje;
    private Boolean leida;
    private LocalDateTime fechaEnvio;

    private Long idUsuario;
    private String nombreUsuario;
}

