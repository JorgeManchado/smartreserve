package com.gestion.reservas.repository;

import com.gestion.reservas.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuarioIdUsuarioOrderByFechaEnvioDesc(Long idUsuario);
}

