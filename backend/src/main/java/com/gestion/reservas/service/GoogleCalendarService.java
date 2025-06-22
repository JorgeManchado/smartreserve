package com.gestion.reservas.service;

import com.gestion.reservas.dto.MisReservasDTO;
import com.gestion.reservas.entity.Reserva;

public interface GoogleCalendarService {

    String insertarEvento(Reserva reserva);
    void eliminarEvento(String eventId);
}
