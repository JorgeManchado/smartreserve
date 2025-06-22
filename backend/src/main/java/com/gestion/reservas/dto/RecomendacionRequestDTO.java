package com.gestion.reservas.dto;

import lombok.Data;

@Data
public class RecomendacionRequestDTO {
    private Long usuarioId;
    private int diaSemana;
    private int hora = -1;  // por defecto -1 si no se envía
}