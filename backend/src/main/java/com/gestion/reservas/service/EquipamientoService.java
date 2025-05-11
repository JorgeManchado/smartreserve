package com.gestion.reservas.service;

import com.gestion.reservas.dto.EquipamientoDTO;

import java.util.List;
import java.util.Optional;

public interface EquipamientoService {
    List<EquipamientoDTO> findAll();
    Optional<EquipamientoDTO> findById(Long id);
}
