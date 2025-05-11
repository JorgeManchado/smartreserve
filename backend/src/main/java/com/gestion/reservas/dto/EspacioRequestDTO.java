package com.gestion.reservas.dto;

import lombok.Data;

import java.util.List;

@Data
public class EspacioRequestDTO {
    private String nombre;
    private Integer capacidad;
    private String ubicacion;
    private String descripcion;
    private String imagen;
    private Long idTipoEspacio;
    private Long idEstado;
    private List<Long> equipamientos; // Lista de IDs
}
