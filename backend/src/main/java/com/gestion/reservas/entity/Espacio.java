package com.gestion.reservas.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "espacios")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

public class Espacio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEspacio;

    private String nombre;

    private Integer capacidad;

    private String ubicacion;

    private String descripcion;

    private String imagen;

    @ManyToOne
    @JoinColumn(name = "id_tipo_espacio")
    private TipoEspacio tipoEspacio;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private EstadoEspacio estado;

    @ManyToMany
    @JoinTable(
            name = "espacio_equipamiento",
            joinColumns = @JoinColumn(name = "id_espacio"),
            inverseJoinColumns = @JoinColumn(name = "id_equipamiento")
    )
    private List<Equipamiento> equipamientos = new ArrayList<>();
}

