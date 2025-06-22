package com.gestion.reservas.repository;

import com.gestion.reservas.entity.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstadoReservaRepository extends JpaRepository<EstadoReserva, Long> {
    Optional<EstadoReserva> findByDescripcionIgnoreCase(String descripcion);

}


