package com.gestion.reservas.service;

import com.gestion.reservas.entity.EstadoUsuario;
import java.util.List;
import java.util.Optional;

public interface EstadoUsuarioService {

    List<EstadoUsuario> findAll();

    Optional<EstadoUsuario> findById(Long id);
}
