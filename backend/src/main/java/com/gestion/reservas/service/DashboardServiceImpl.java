package com.gestion.reservas.service;

import com.gestion.reservas.dto.DashBoardDTO;
import com.gestion.reservas.dto.KPIDTO;
import com.gestion.reservas.dto.TipoSalaDistribucionDTO;
import com.gestion.reservas.dto.UltimaReservaDTO;
import com.gestion.reservas.entity.Reserva;
import com.gestion.reservas.repository.EspacioRepository;
import com.gestion.reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ReservaRepository reservaRepository;
    private final EspacioRepository espacioRepository;

    @Override
    public DashBoardDTO obtenerDatosDashboard(LocalDate fechaInicio, LocalDate fechaFin, Long tipoEspacioId, Long estadoId) {

        LocalDateTime fechaInicioDT = (fechaInicio != null)
                ? fechaInicio.atStartOfDay()
                : reservaRepository.obtenerFechaInicioMinima();

        LocalDateTime fechaFinDT = (fechaFin != null)
                ? fechaFin.atTime(LocalTime.MAX)
                : reservaRepository.obtenerFechaFinMaxima();

        List<Reserva> reservas = reservaRepository.buscarPorFiltros(fechaInicioDT, fechaFinDT, tipoEspacioId, estadoId);
        int espaciosDisponibles = espacioRepository.countByEstado_IdEstado(1L);

        return DashBoardDTO.builder()
                .kpi(calcularKPI(reservas, espaciosDisponibles, fechaInicioDT.toLocalDate(), fechaFinDT.toLocalDate()))
                .ultimasReservas(mapUltimasReservas(reservas))
                .reservasPeriodo(agruparPorDiaSemana(reservas))
                .tipoSalaDistribucion(agruparPorTipoSala(reservas))
                .build();
    }

    private KPIDTO calcularKPI(List<Reserva> reservas, int espaciosDisponibles, LocalDate fechaInicio, LocalDate fechaFin) {




        long diasPeriodo = ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
        return KPIDTO.builder()
                .totalReservas(reservas.size()) // Total de reservas realizadas
                .tasaOcupacion(calcularTasaOcupacion(reservas, espaciosDisponibles, diasPeriodo)) //Calcula la tasa de ocupación con base en horas usadas / horas disponibles.
                .usuariosActivos((int) reservas.stream().map(r -> r.getUsuario().getIdUsuario()).distinct().count()) // Usuarios únicos que realizarón reservas
                .horasReservadas(reservas.stream()
                        .mapToInt(r -> (int) Duration.between(r.getFechaInicio(), r.getFechaFin()).toHours())
                        .sum()) // Se suman las horas totales entre fechaInicio y fechaFin de cada reserva.


                .build();
    }

    private List<UltimaReservaDTO> mapUltimasReservas(List<Reserva> reservas) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return reservas.stream()
                .sorted(Comparator.comparing(Reserva::getIdReserva).reversed())
                .limit(5)
                .map(r -> {
                    long horas = Duration.between(r.getFechaInicio(), r.getFechaFin()).toHours();
                    return UltimaReservaDTO.builder()
                            .usuario(r.getUsuario().getNombre())
                            .sala(r.getEspacio().getNombre())
                            .fecha(r.getFechaInicio().toLocalDate().format(formatter))
                            .duracion(horas + " horas")
                            .estado(r.getEstado().getDescripcion())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<Integer> agruparPorDiaSemana(List<Reserva> reservas) {
        Map<DayOfWeek, Long> conteo = reservas.stream()
                .collect(Collectors.groupingBy(r -> r.getFechaInicio().getDayOfWeek(), Collectors.counting()));

        List<DayOfWeek> dias = Arrays.asList(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY
        );

        return dias.stream()
                .map(d -> conteo.getOrDefault(d, 0L).intValue())
                .collect(Collectors.toList());
    }

    private List<TipoSalaDistribucionDTO> agruparPorTipoSala(List<Reserva> reservas) {
        Map<String, Long> conteo = reservas.stream()
                .collect(Collectors.groupingBy(r -> r.getEspacio().getTipoEspacio().getDescripcion(), Collectors.counting()));

        return conteo.entrySet().stream()
                .map(e -> new TipoSalaDistribucionDTO(e.getKey(), e.getValue().intValue()))
                .sorted(Comparator.comparing(TipoSalaDistribucionDTO::getTipo))
                .collect(Collectors.toList());
    }


    private int calcularTasaOcupacion(List<Reserva> reservas, int espaciosActivos, long diasPeriodo) {
        if (espaciosActivos <= 0 || diasPeriodo <= 0) return 0;

        long horasReservadas = reservas.stream()
                .filter(r -> r.getFechaInicio() != null && r.getFechaFin() != null)
                .mapToLong(r -> Duration.between(r.getFechaInicio(), r.getFechaFin()).toHours())
                .sum();

        long horasDisponibles = espaciosActivos * 12L * diasPeriodo;

        return (int) Math.round((double) horasReservadas / horasDisponibles * 100);
    }

}
