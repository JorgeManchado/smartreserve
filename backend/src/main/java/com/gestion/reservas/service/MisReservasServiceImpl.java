package com.gestion.reservas.service;

import com.gestion.reservas.dto.MisReservasDTO;
import com.gestion.reservas.dto.ReservaDTO;
import com.gestion.reservas.entity.Espacio;
import com.gestion.reservas.entity.EstadoReserva;
import com.gestion.reservas.entity.Reserva;
import com.gestion.reservas.entity.Usuario;
import com.gestion.reservas.mapper.MisReservasMapper;
import com.gestion.reservas.repository.EspacioRepository;
import com.gestion.reservas.repository.EstadoReservaRepository;
import com.gestion.reservas.repository.ReservaRepository;
import com.gestion.reservas.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MisReservasServiceImpl implements MisReservasService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EspacioRepository espacioRepository;
    private final EstadoReservaRepository estadoReservaRepository;
    private final NotificacionService notificacionService;
    private final MisReservasMapper mapper;
    private final GoogleCalendarService googleCalendarService;

    @Override
    public List<MisReservasDTO> findAll() {
        return reservaRepository.findAllByOrderByIdReservaDesc()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MisReservasDTO> findById(Long id) {
        return reservaRepository.findById(id)
                .map(mapper::toDTO);
    }

    @Override
    public List<MisReservasDTO> obtenerReservasPorUsuario(Long idUsuario) {
        List<Reserva> reservas = reservaRepository.findByUsuarioIdUsuario(idUsuario);
        return reservas.stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MisReservasDTO save(MisReservasDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuario().getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Espacio espacio = espacioRepository.findById(dto.getEspacio().getIdEspacio())
                .orElseThrow(() -> new IllegalArgumentException("Espacio no encontrado"));

        EstadoReserva estado = estadoReservaRepository.findById(dto.getEstado().getIdEstado())
                .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado"));

        Reserva reserva = mapper.toEntity(dto, usuario, espacio, estado);

        // Sincronizar con Google Calendar antes de guardar si está activado
        if (dto.isSincronizado()) {
            String eventoId = googleCalendarService.insertarEvento(reserva);
            reserva.setEventid(eventoId);
        }


        Reserva saved = reservaRepository.save(reserva);

        String mensaje = String.format(
                "Tu reserva #%S en el espacio %s para el día %s ha sido registrada.",
                reserva.getIdReserva(),
                reserva.getEspacio().getNombre(),
                reserva.getFechaInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
        notificacionService.crearYEnviarNotificacion(usuario, mensaje);



        return mapper.toDTO(saved);
    }

    @Override
    public void deleteById(Long id) {
        reservaRepository.deleteById(id);
    }

    public void cancelarReserva(Long idReserva) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada"));

        EstadoReserva estadoCancelado = estadoReservaRepository.findByDescripcionIgnoreCase("Cancelada")
                .orElseThrow(() -> new IllegalStateException("Estado 'Cancelada' no configurado"));


        // Si tiene evento de Google Calendar, lo eliminamos
        if (reserva.getEventid() != null) {
            try {
                googleCalendarService.eliminarEvento(reserva.getEventid());
                reserva.setEventid(null);
            } catch (Exception e) {
                 System.err.println("No se pudo eliminar el evento de Google Calendar: " + e.getMessage());
            }
        }

        reserva.setEstado(estadoCancelado);
        reservaRepository.save(reserva);

        Usuario usuario = usuarioRepository.findById(reserva.getUsuario().getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));


        String mensaje = String.format(
                "Se ha cancelado la reserva #%S del espacio  %s el día %s.",
                reserva.getIdReserva(),
                reserva.getEspacio().getNombre(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))

        );
        notificacionService.crearYEnviarNotificacion(usuario, mensaje);

    }


    public void confirmarReserva(Long idReserva) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada"));

        EstadoReserva estadoConfirmada = estadoReservaRepository.findByDescripcionIgnoreCase("Confirmada")
                .orElseThrow(() -> new IllegalStateException("Estado 'Confirmada' no encontrado"));

        reserva.setEstado(estadoConfirmada);
        reservaRepository.save(reserva);

        Usuario usuario = usuarioRepository.findById(reserva.getUsuario().getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));


        String mensaje = String.format(
                "Se ha confirmado la reserva #%S del espacio  %s el día %s.",
                reserva.getIdReserva(),
                reserva.getEspacio().getNombre(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))

        );
        notificacionService.crearYEnviarNotificacion(usuario, mensaje);

    }

    public boolean estaDisponible(Long idEspacio, String inicioStr, String finStr) {
        // Parsear las fechas
        LocalDateTime inicio = LocalDateTime.parse(inicioStr);
        LocalDateTime fin = LocalDateTime.parse(finStr);

        // Buscar reservas del espacio que se solapen con ese rango
        List<Reserva> reservasSolapadas = reservaRepository.findByEspacioIdAndRangoFechasSolapado(idEspacio, inicio, fin);

        // Si no hay reservas solapadas, está disponible
        return reservasSolapadas.isEmpty();
    }

    @Scheduled(cron = "0 */5 * * * *") // Cada 5 minutos
    public void actualizarReservasVencidas() {
        LocalDateTime ahora = LocalDateTime.now();

        EstadoReserva estadoPendiente = estadoReservaRepository.findByDescripcionIgnoreCase("Pendiente")
                .orElseThrow(() -> new IllegalStateException("Estado 'Pendiente' no encontrado"));

        EstadoReserva estadoConfirmada = estadoReservaRepository.findByDescripcionIgnoreCase("Confirmada")
                .orElseThrow(() -> new IllegalStateException("Estado 'Confirmada' no encontrado"));

        EstadoReserva estadoNoUtilizada = estadoReservaRepository.findByDescripcionIgnoreCase("No Utilizada")
                .orElseThrow(() -> new IllegalStateException("Estado 'Cancelada Aut.' no encontrado"));

        EstadoReserva estadoCompletada = estadoReservaRepository.findByDescripcionIgnoreCase("Completada")
                .orElseThrow(() -> new IllegalStateException("Estado 'Completada' no encontrado"));

        // --- Pendientes vencidas
        List<Reserva> pendientesVencidas = reservaRepository.findByEstadoAndFechaFinBefore(estadoPendiente, ahora);
        for (Reserva reserva : pendientesVencidas) {
            reserva.setEstado(estadoNoUtilizada);
            reservaRepository.save(reserva);

            Usuario usuario = reserva.getUsuario();
            String mensaje = String.format(
                    "Tu reserva #%s del espacio %s ha sido cancelada automáticamente por no haber sido confirmada.",
                    reserva.getIdReserva(),
                    reserva.getEspacio().getNombre()
            );
            notificacionService.crearYEnviarNotificacion(usuario, mensaje);
        }

        // --- Confirmadas vencidas
        List<Reserva> confirmadasVencidas = reservaRepository.findByEstadoAndFechaFinBefore(estadoConfirmada, ahora);
        for (Reserva reserva : confirmadasVencidas) {
            reserva.setEstado(estadoCompletada);
            reservaRepository.save(reserva);

            Usuario usuario = reserva.getUsuario();
            String mensaje = String.format(
                    "Tu reserva #%s del espacio %s ha sido completada correctamente.",
                    reserva.getIdReserva(),
                    reserva.getEspacio().getNombre()
            );
            notificacionService.crearYEnviarNotificacion(usuario, mensaje);
        }
    }


    @Scheduled(cron = "0 */5 * * * *") // Cada 5 minutos
    public void notificarReservasPendientesProximas() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime desde = ahora.plusDays(1).minusMinutes(1);
        LocalDateTime hasta = ahora.plusDays(1).plusMinutes(1);

        Optional<EstadoReserva> estadoPendienteOpt = estadoReservaRepository.findByDescripcionIgnoreCase("Pendiente");

        if (estadoPendienteOpt.isEmpty()) {
            System.out.println("Estado 'Pendiente' no encontrado. Se omite la notificación de reservas próximas.");
            return; // Finaliza sin error
        }

        EstadoReserva estadoPendiente = estadoPendienteOpt.get();

        List<Reserva> reservasProximas = reservaRepository.findByEstadoAndFechaInicioBetween(
                estadoPendiente, desde, hasta
        );

        for (Reserva reserva : reservasProximas) {
            Usuario usuario = reserva.getUsuario();
            String mensaje = String.format(
                    "Recuerda que tu reserva #%s del espacio %s comienza en 24 horas. ¡No olvides confirmarla!",
                    reserva.getIdReserva(),
                    reserva.getEspacio().getNombre()
            );
            notificacionService.crearYEnviarNotificacion(usuario, mensaje);
        }
    }




}
