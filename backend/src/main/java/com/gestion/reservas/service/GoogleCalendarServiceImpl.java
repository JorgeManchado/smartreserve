package com.gestion.reservas.service;

import com.gestion.reservas.entity.Reserva;
import com.gestion.reservas.security.GoogleTokenStore;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.time.ZoneId;

import java.util.Date;


@Service
@RequiredArgsConstructor
public class GoogleCalendarServiceImpl implements GoogleCalendarService {

    private final GoogleTokenStore tokenStore;

    @Value("${google.application.name}")
    private String applicationName;

    public String insertarEvento(Reserva reserva) {
        try {
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            GoogleCredential credential = new GoogleCredential()
                    .setAccessToken(tokenStore.getAccessToken());

            Calendar calendar = new Calendar.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName(applicationName)
                    .build();

            Event event = new Event()
                    .setSummary("Reserva de sala: " + reserva.getEspacio().getNombre())
                    .setDescription("Reserva de " + reserva.getUsuario().getNombre());

            DateTime start = new DateTime(Date.from(reserva.getFechaInicio()
                    .atZone(ZoneId.systemDefault()).toInstant()));
            DateTime end = new DateTime(Date.from(reserva.getFechaFin()
                    .atZone(ZoneId.systemDefault()).toInstant()));

            event.setStart(new EventDateTime().setDateTime(start).setTimeZone("Europe/Madrid"));
            event.setEnd(new EventDateTime().setDateTime(end).setTimeZone("Europe/Madrid"));



            Event createdEvent = calendar.events().insert("primary", event).execute();
            return createdEvent.getId();

        } catch (Exception e) {
            throw new RuntimeException("Error al sincronizar con Google Calendar", e);
        }
    }


    public void eliminarEvento(String eventId) {
        try {
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            GoogleCredential credential = new GoogleCredential()
                    .setAccessToken(tokenStore.getAccessToken());

            Calendar calendar = new Calendar.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName(applicationName)
                    .build();

            calendar.events().delete("primary", eventId).execute();

        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el evento de Google Calendar", e);
        }
    }

}

