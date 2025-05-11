package com.gestion.reservas.websocket;

import com.gestion.reservas.dto.NotificacionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificacionWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public void enviarNotificacion(NotificacionDTO noti, Long idUsuario) {
        messagingTemplate.convertAndSend("/topic/notificaciones/" + idUsuario, noti);
    }
}

