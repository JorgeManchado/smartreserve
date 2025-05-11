package com.gestion.reservas.mapper;

import com.gestion.reservas.dto.NotificacionDTO;
import com.gestion.reservas.entity.Notificacion;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class NotificacionMapper {

    public NotificacionDTO toDto(Notificacion entity) {
        if (entity == null) return null;

        NotificacionDTO dto = new NotificacionDTO();
        dto.setIdNotificacion(entity.getIdNotificacion());
        dto.setMensaje(entity.getMensaje());
        dto.setLeida(entity.getLeida());
        dto.setFechaEnvio(entity.getFechaEnvio());

        if (entity.getUsuario() != null) {
            dto.setIdUsuario(entity.getUsuario().getIdUsuario());
            dto.setNombreUsuario(entity.getUsuario().getNombre());
        }

        return dto;
    }


    public Notificacion toEntity(NotificacionDTO dto) {
        return null;
    }
}

