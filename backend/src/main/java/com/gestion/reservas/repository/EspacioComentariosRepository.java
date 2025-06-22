package com.gestion.reservas.repository;

import com.gestion.reservas.entity.Espacio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EspacioComentariosRepository extends JpaRepository<Espacio, Long>, EspacioComentariosRepositoryCustom {}
