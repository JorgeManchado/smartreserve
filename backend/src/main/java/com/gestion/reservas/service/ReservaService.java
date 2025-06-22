package com.gestion.reservas.service;

import com.gestion.reservas.dto.ReservaCalendarioDTO;
import com.gestion.reservas.dto.ReservaDTO;
import com.gestion.reservas.entity.Usuario;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservaService {
    List<ReservaDTO> findAll();
    Optional<ReservaDTO> findById(Long id);
    ReservaDTO save(ReservaDTO reservaDTO);
    void deleteById(Long id);
    List<ReservaCalendarioDTO> obtenerReservasEntreFechas(LocalDateTime desde, LocalDateTime hasta);

}
