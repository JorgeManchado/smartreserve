package com.gestion.reservas.service;

import com.gestion.reservas.dto.NotificacionDTO;
import com.gestion.reservas.dto.ReservaDTO;
import com.gestion.reservas.entity.*;
import com.gestion.reservas.mapper.NotificacionMapper;
import com.gestion.reservas.repository.*;
import com.gestion.reservas.websocket.NotificacionWebSocketController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EspacioRepository espacioRepository;
    private final EstadoReservaRepository estadoReservaRepository;
    private final NotificacionService notificacionService;
    @Override
    public List<ReservaDTO> findAll() {
        return reservaRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ReservaDTO> findById(Long id) {
        return reservaRepository.findById(id)
                .map(this::toDTO);
    }

    public ReservaDTO save(ReservaDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Espacio espacio = espacioRepository.findById(dto.getIdEspacio())
                .orElseThrow(() -> new IllegalArgumentException("Espacio no encontrado"));

        EstadoReserva estado = estadoReservaRepository.findById(dto.getIdEstado())
                .orElseThrow(() -> new IllegalArgumentException("Estado de reserva no encontrado"));

        Reserva reserva = toEntity(dto, usuario, espacio, estado);
        Reserva saved = reservaRepository.save(reserva);

        // Crear notificación
       String mensaje = String.format(
                "Tu reserva en %s para el día %s ha sido registrada.",
                reserva.getEspacio().getNombre(),
                reserva.getFechaInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
        notificacionService.crearYEnviarNotificacion(usuario, mensaje);

        return toDTO(saved);
    }


    @Override
    public void deleteById(Long id) {
        reservaRepository.deleteById(id);
    }

    public ReservaDTO toDTO(Reserva reserva) {
        return new ReservaDTO(
                reserva.getIdReserva(),
                reserva.getUsuario().getIdUsuario(),
                reserva.getUsuario().getNombre(),
                reserva.getUsuario().getTelefono(),
                reserva.getEspacio().getIdEspacio(),
                reserva.getEspacio().getNombre(),
                reserva.getEspacio().getImagen(),
                reserva.getEstado().getIdEstado(),
                reserva.getEstado().getDescripcion(),
                reserva.getFechaInicio(),
                reserva.getFechaFin(),
                reserva.getSincronizado()
        );
    }

    public Reserva toEntity(ReservaDTO dto, Usuario usuario, Espacio espacio, EstadoReserva estado) {
        Reserva reserva = new Reserva();
        reserva.setIdReserva(dto.getIdReserva());
        reserva.setUsuario(usuario);
        reserva.setEspacio(espacio);
        reserva.setEstado(estado);
        reserva.setFechaInicio(dto.getFechaInicio());
        reserva.setFechaFin(dto.getFechaFin());
        reserva.setSincronizado(dto.getSincronizado());
        return reserva;
    }
}

