package com.gestion.reservas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KPIDTO {
    private int totalReservas;
    private int tasaOcupacion;
    private int usuariosActivos;
    private int horasReservadas;
}
