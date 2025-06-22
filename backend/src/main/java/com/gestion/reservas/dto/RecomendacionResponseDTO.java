package com.gestion.reservas.dto;

import lombok.Data;
import java.util.List;

@Data
public class RecomendacionResponseDTO {
    private Long usuario_id;
    private String nombre_usuario;
    private int dia_semana;
    private int hora;
    private List<EspacioRecomendadoDTO> recomendaciones;
}
