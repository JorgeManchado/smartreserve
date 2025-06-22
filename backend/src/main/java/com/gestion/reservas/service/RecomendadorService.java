package com.gestion.reservas.service;

import com.gestion.reservas.dto.RecomendacionResponseDTO;

public interface RecomendadorService {
    RecomendacionResponseDTO obtenerRecomendaciones(Long usuarioId, int diaSemana, int hora) throws Exception;
    String entrenarModelo() throws Exception;
}