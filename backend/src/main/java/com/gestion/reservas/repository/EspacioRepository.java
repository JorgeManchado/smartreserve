package com.gestion.reservas.repository;

import com.gestion.reservas.entity.Espacio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EspacioRepository extends JpaRepository<Espacio, Long> {
    int countByEstado_IdEstado(Long idEstado);
}

