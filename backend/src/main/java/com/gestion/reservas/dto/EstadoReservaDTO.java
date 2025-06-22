package com.gestion.reservas.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EstadoReservaDTO {
    private Long idEstado;
    private String descripcion;
    private String color;
    private String bgcolor;
}

