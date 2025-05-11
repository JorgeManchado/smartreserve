package com.gestion.reservas.service;


import com.gestion.reservas.entity.EstadoReserva;

import java.util.List;
import java.util.Optional;

public interface EstadoReservaService {
    List<EstadoReserva> findAll();

    Optional<EstadoReserva> findById(Long id);
}

