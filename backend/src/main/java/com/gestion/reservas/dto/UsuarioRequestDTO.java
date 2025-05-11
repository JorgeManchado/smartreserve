package com.gestion.reservas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioRequestDTO {

    @NotBlank
    private String nombre;

    @Email
    @NotBlank
    private String email;

    private String password;

    private String telefono;
    private String direccion;
    private Long idRol;
    private Long idEstado;
}
