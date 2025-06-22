package com.gestion.reservas.repository;

import com.gestion.reservas.entity.Espacio;
import com.gestion.reservas.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EspacioRepository extends JpaRepository<Espacio, Long> {
    int countByEstado_IdEstado(Long idEstado);
    List<Espacio> findAllByOrderByIdEspacioDesc();
}

