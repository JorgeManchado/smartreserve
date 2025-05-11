package com.gestion.reservas.service;

import com.gestion.reservas.dto.ComentarioDTO;
import com.gestion.reservas.entity.Comentario;
import com.gestion.reservas.entity.EstadoComentario;
import com.gestion.reservas.mapper.ComentarioMapper;
import com.gestion.reservas.repository.ComentarioRepository;
import com.gestion.reservas.repository.EstadoComentarioRepository;
import com.gestion.reservas.service.ComentarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComentarioServiceImpl implements ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final EstadoComentarioRepository estadoComentarioRepository;
    private final ComentarioMapper comentarioMapper;
    private final NotificacionService notificacionService;

    @Override
    public List<ComentarioDTO> listarComentarios() {
        return comentarioRepository.findAllByOrderByFechaDesc()
                .stream()
                .map(comentarioMapper::toDto)
                .toList();
    }

    @Override
    public void aprobarComentario(Long id) {
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));

        EstadoComentario aprobado = estadoComentarioRepository.findByDescripcionIgnoreCase("Aprobado")
                .orElseThrow(() -> new RuntimeException("Estado 'Aprobado' no encontrado"));

        comentario.setEstado(aprobado);
        comentarioRepository.save(comentario);

        // Crear notificación
        String mensaje = String.format(
                "Comentario de la reserva #%s aprobado el día %s.",
                comentario.getReserva().getIdReserva(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );

        notificacionService.crearYEnviarNotificacion(comentario.getReserva().getUsuario(), mensaje);
    }

    @Override
    public void anularComentario(Long id) {
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));

        EstadoComentario anulado = estadoComentarioRepository.findByDescripcionIgnoreCase("Anulado")
                .orElseThrow(() -> new RuntimeException("Estado 'Anulado' no encontrado"));

        comentario.setEstado(anulado);
        comentarioRepository.save(comentario);

        // Crear notificación
        String mensaje = String.format(
                "Comentario de la reserva #%s anulado el día %s.",
                comentario.getReserva().getIdReserva(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );

        notificacionService.crearYEnviarNotificacion(comentario.getReserva().getUsuario(), mensaje);
    }
}
