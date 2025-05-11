package com.gestion.reservas.repository;

import com.gestion.reservas.entity.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findAllByOrderByFechaDesc();
}
