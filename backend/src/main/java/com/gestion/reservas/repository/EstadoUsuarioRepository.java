package com.gestion.reservas.repository;

import com.gestion.reservas.entity.EstadoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstadoUsuarioRepository extends JpaRepository<EstadoUsuario, Long> {
    Optional<EstadoUsuario> findByDescripcion(String descripcion);
}

