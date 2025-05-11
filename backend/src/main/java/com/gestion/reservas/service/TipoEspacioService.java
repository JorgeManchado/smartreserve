package com.gestion.reservas.service;

import com.gestion.reservas.entity.TipoEspacio;

import java.util.List;
import java.util.Optional;

public interface TipoEspacioService {
    List<TipoEspacio> findAll();

    Optional<TipoEspacio> findById(Long id);
}
