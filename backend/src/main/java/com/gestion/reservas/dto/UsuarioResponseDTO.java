package com.gestion.reservas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class UsuarioResponseDTO {
    private Long idUsuario;
    private String nombre;
    private String email;
    private String telefono;
    private String direccion;
    private LocalDateTime ultimoAcceso;
    private Long idRol;
    private String rol;
    private Long idEstado;
    private String estado;
}
