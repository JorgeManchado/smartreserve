package com.gestion.reservas.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MisReservasDTO {
    private Long idReserva;
    private UsuarioResponseDTO usuario;
    private EspacioResponseDTO espacio;
    private EstadoReservaDTO estado;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private boolean sincronizado;
    private int ocupantes;
    private boolean recomendadaia;
    private String eventid;
}
