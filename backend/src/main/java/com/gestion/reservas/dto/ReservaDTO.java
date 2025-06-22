package com.gestion.reservas.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaDTO {

    private Long idReserva;

    private Long idUsuario;
    private String nombreUsuario;
    private String telefonoUsuario;

    private Long idEspacio;
    private String nombreEspacio;
    private String imagenEspacio;

    private Long idEstado;
    private String descripcionEstado;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    private Boolean sincronizado;
    private int ocupantes;
    private Boolean recomendadaia;
    private String eventid;
}

