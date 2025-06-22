package com.gestion.reservas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UltimaReservaDTO {
    private String usuario;
    private String sala;
    private String fecha;
    private String duracion;
    private String estado;
    private String color;
    private String bgcolor;
}
