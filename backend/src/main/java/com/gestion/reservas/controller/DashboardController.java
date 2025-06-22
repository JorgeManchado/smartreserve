package com.gestion.reservas.controller;

import com.gestion.reservas.dto.DashBoardDTO;
import com.gestion.reservas.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public DashBoardDTO obtenerDashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Long tipoSala,
            @RequestParam(required = false) Long estado
    ) {

        return dashboardService.obtenerDatosDashboard(fechaInicio, fechaFin, tipoSala, estado);
    }
}