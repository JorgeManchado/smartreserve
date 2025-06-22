package com.gestion.reservas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservaCalendarioDTO{
    private Long idReserva;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String sala;
    private String usuario;
    private String estado;
    private String color;
    private String bgcolor;
    private String tipo;
}
