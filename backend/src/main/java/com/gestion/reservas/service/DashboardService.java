package com.gestion.reservas.service;

import com.gestion.reservas.dto.DashBoardDTO;

import java.time.LocalDate;

public interface DashboardService {
    DashBoardDTO obtenerDatosDashboard(LocalDate fechaInicio, LocalDate fechaFin, Long tipoEspacioId, Long estadoId);
}