package com.gestion.reservas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashBoardDTO {
    private KPIDTO kpi;
    private List<UltimaReservaDTO> ultimasReservas;
    private List<Integer> reservasPeriodo;
    private List<TipoSalaDistribucionDTO> tipoSalaDistribucion;
}