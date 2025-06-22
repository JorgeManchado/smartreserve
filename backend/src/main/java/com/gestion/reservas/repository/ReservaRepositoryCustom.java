package com.gestion.reservas.repository;


import com.gestion.reservas.entity.Reserva;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaRepositoryCustom {
    List<Reserva> buscarPorFiltros(LocalDateTime fechaInicio, LocalDateTime fechaFin, Long tipoEspacioId, Long estadoId);
}
