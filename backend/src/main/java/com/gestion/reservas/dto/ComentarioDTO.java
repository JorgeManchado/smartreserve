package com.gestion.reservas.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComentarioDTO {
    private Long idComentario;
    private String texto;
    private Integer valoracion;
    private LocalDateTime fecha;

    private String estadoDescripcion;
    private Long idEstado;

    private String nombreUsuario;
    private Long idUsuario;

    private Long idReserva;
}
