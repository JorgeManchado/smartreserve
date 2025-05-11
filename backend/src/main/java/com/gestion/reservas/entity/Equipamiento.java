package com.gestion.reservas.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "equipamientos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEquipamiento;

    private String descripcion;
}

