package com.gestion.reservas.repository;

import com.gestion.reservas.entity.EstadoComentario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstadoComentarioRepository extends JpaRepository<EstadoComentario, Long> {
    Optional<EstadoComentario> findByDescripcionIgnoreCase(String descripcion);
}

