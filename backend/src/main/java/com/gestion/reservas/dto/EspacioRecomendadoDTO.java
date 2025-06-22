package com.gestion.reservas.dto;

import lombok.Data;

@Data
public class EspacioRecomendadoDTO {
    private Long espacio_id;
    private String nombre_espacio;
    private double probabilidad;
}
