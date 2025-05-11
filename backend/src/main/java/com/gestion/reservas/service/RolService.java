package com.gestion.reservas.service;

import com.gestion.reservas.entity.Rol;
import java.util.List;
import java.util.Optional;

public interface RolService {
    List<Rol> findAll();

    Optional<Rol> findById(Long id);
}
