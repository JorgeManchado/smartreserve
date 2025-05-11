package com.gestion.reservas.controller;

import com.gestion.reservas.dto.NotificacionDTO;
import com.gestion.reservas.entity.Notificacion;
import com.gestion.reservas.service.NotificacionService;
import com.gestion.reservas.service.NotificacionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificacionController {

    private final NotificacionServiceImpl notificacionService;

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<NotificacionDTO>> getNotificacionesUsuario(@PathVariable Long idUsuario) {
        List<NotificacionDTO> notificacionesDTO =
                notificacionService.toDtoList(notificacionService.findByUsuarioId(idUsuario));
        return ResponseEntity.ok(notificacionesDTO);
    }

    @PutMapping("/{id}/leida")
    public ResponseEntity<Void> marcarComoLeida(@PathVariable Long id) {
        notificacionService.marcarComoLeida(id);
        return ResponseEntity.noContent().build();
    }
}
