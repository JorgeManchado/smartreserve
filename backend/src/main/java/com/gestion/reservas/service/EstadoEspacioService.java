package com.gestion.reservas.service;

import com.gestion.reservas.entity.EstadoEspacio;

import java.util.List;
import java.util.Optional;

public interface EstadoEspacioService {
    List<EstadoEspacio> findAll();

    Optional<EstadoEspacio> findById(Long id);
}
