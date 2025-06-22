package com.gestion.reservas.repository;

import com.gestion.reservas.entity.EstadoReserva;
import com.gestion.reservas.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long>, ReservaRepositoryCustom {

    @Query("SELECT MIN(r.fechaInicio) FROM Reserva r")
    LocalDateTime obtenerFechaInicioMinima();

    @Query("SELECT MAX(r.fechaFin) FROM Reserva r")
    LocalDateTime obtenerFechaFinMaxima();

    @Query("""
      SELECT r FROM Reserva r
      WHERE r.espacio.idEspacio = :idEspacio
        AND r.estado.descripcion != 'Cancelada'
        AND (
          (r.fechaInicio <= :fin AND r.fechaFin >= :inicio)
        )
    """)
    List<Reserva> findByEspacioIdAndRangoFechasSolapado(
            @Param("idEspacio") Long idEspacio,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );

    List<Reserva> findAllByOrderByIdReservaDesc();

    List<Reserva> findByFechaInicioBetween(LocalDateTime inicio, LocalDateTime fin);
    List<Reserva> findByEstadoAndFechaFinBefore(EstadoReserva estado, LocalDateTime fechaFin);

    List<Reserva> findByUsuarioIdUsuario(Long idUsuario);

    List<Reserva> findByEstadoAndFechaInicioBetween(EstadoReserva estado, LocalDateTime desde, LocalDateTime hasta);
}
