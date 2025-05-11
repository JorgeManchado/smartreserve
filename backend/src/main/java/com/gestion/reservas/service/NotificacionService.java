package com.gestion.reservas.service;

import com.gestion.reservas.entity.Notificacion;
import com.gestion.reservas.entity.Usuario;

import java.util.List;

public interface NotificacionService {

    List<Notificacion> findByUsuarioId(Long idUsuario);

    void crearYEnviarNotificacion(Usuario usuario, String mensaje);
    void marcarComoLeida(Long idNotificacion);
}
