package com.gestion.reservas.dto;

import lombok.*;

import java.util.List;

import lombok.Data;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EspacioResponseDTO {
    private Long idEspacio;
    private String nombre;
    private Integer capacidad;
    private String ubicacion;
    private String descripcion;
    private String imagen;

    private TipoEspacioDTO tipo;
    private EstadoEspacioDTO estado;
    private List<EquipamientoDTO> equipamientos;
}

