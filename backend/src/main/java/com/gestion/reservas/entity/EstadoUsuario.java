package com.gestion.reservas.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "estados_usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado")
    private Long idEstado;

    @Column(nullable = false, unique = true, length = 100)
    private String descripcion;
}

