package com.gestion.reservas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class EspacioComentariosDTO {
    private Long idEspacio;
    private String nombre;
    private String imagen;
    private Double valoracionPromedio;
    private Long cantidadResenas;

    private List<ComentarioDTO> comentarios;

    public EspacioComentariosDTO(Long idEspacio, String nombre, String imagen, Double valoracionPromedio, Long cantidadResenas) {
        this.idEspacio = idEspacio;
        this.nombre = nombre;
        this.imagen = imagen;
        this.valoracionPromedio = valoracionPromedio;
        this.cantidadResenas = cantidadResenas;
        this.comentarios = new ArrayList<>();
    }
}