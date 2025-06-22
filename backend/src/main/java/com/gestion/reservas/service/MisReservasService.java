package com.gestion.reservas.service;

import com.gestion.reservas.dto.MisReservasDTO;
import com.gestion.reservas.dto.ReservaDTO;

import java.util.List;
import java.util.Optional;

public interface MisReservasService {

    List<MisReservasDTO> findAll();

    Optional<MisReservasDTO> findById(Long id);

    MisReservasDTO save(MisReservasDTO misreservaDTO);

    void deleteById(Long id);

    void cancelarReserva(Long idReserva);

    void confirmarReserva(Long idReserva);

    boolean estaDisponible(Long idEspacio, String inicioStr, String finStr);

    List<MisReservasDTO> obtenerReservasPorUsuario(Long idUsuario);

}


