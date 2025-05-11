package com.gestion.reservas.service;

import com.gestion.reservas.dto.NotificacionDTO;
import com.gestion.reservas.entity.Notificacion;
import com.gestion.reservas.entity.Usuario;
import com.gestion.reservas.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.gestion.reservas.websocket.NotificacionWebSocketController;
import com.gestion.reservas.mapper.NotificacionMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final NotificacionWebSocketController notificacionWebSocketController;
    private final NotificacionMapper notificacionMapper;

    @Override
    public List<Notificacion> findByUsuarioId(Long idUsuario) {
        return notificacionRepository.findByUsuarioIdUsuarioOrderByFechaEnvioDesc(idUsuario);
    }

    @Override
    public void marcarComoLeida(Long idNotificacion) {
        notificacionRepository.findById(idNotificacion).ifPresent(noti -> {
            noti.setLeida(true);
            notificacionRepository.save(noti);
        });
    }

    public NotificacionDTO toDto(Notificacion entity) {
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

    public List<NotificacionDTO> toDtoList(List<Notificacion> entidades) {
        return entidades.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }


    public void crearYEnviarNotificacion(Usuario usuario, String mensaje) {

        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuario);
        notificacion.setMensaje(mensaje);
        notificacion.setLeida(false);
        notificacion.setFechaEnvio(LocalDateTime.now());

        notificacion = notificacionRepository.save(notificacion);
        NotificacionDTO notificacionDto = notificacionMapper.toDto(notificacion);
        notificacionWebSocketController.enviarNotificacion(notificacionDto, notificacionDto.getIdUsuario());
    }
}

