package com.gestion.reservas.controller;

import com.gestion.reservas.dto.MisReservasDTO;
import com.gestion.reservas.security.JwtAuthenticationFilter;
import com.gestion.reservas.service.MisReservasService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(
        controllers = MisReservasController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
class MisReservasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MisReservasService reservaService;

    @Test
    void testGetAllReservas() throws Exception {
        MisReservasDTO dto = MisReservasDTO.builder()
                .idReserva(1L)
                .fechaInicio(LocalDateTime.now())
                .fechaFin(LocalDateTime.now().plusHours(1))
                .ocupantes(2)
                .sincronizado(false)
                .recomendadaia(false)
                .eventid("evt123")
                .build();

        Mockito.when(reservaService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/misreservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].idReserva").value(1));
    }

    @Test
    void testGetById_found() throws Exception {
        MisReservasDTO dto = MisReservasDTO.builder().idReserva(1L).build();

        Mockito.when(reservaService.findById(1L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/misreservas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idReserva").value(1));
    }

    @Test
    void testGetById_notFound() throws Exception {
        Mockito.when(reservaService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/misreservas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateReserva() throws Exception {
        MisReservasDTO input = MisReservasDTO.builder().ocupantes(3).build();
        MisReservasDTO output = MisReservasDTO.builder().idReserva(10L).ocupantes(3).build();

        Mockito.when(reservaService.save(any())).thenReturn(output);

        mockMvc.perform(post("/api/misreservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "ocupantes": 3
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idReserva").value(10));
    }

    @Test
    void testUpdateReserva_notFound() throws Exception {
        Mockito.when(reservaService.findById(5L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/misreservas/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "ocupantes": 3
                            }
                        """))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteReserva_found() throws Exception {
        Mockito.when(reservaService.findById(1L)).thenReturn(Optional.of(new MisReservasDTO()));

        mockMvc.perform(delete("/api/misreservas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteReserva_notFound() throws Exception {
        Mockito.when(reservaService.findById(123L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/misreservas/123"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCancelarReserva() throws Exception {
        mockMvc.perform(put("/api/misreservas/1/cancelar"))
                .andExpect(status().isNoContent());

        Mockito.verify(reservaService).cancelarReserva(1L);
    }

    @Test
    void testConfirmarReserva() throws Exception {
        mockMvc.perform(put("/api/misreservas/1/confirmar"))
                .andExpect(status().isOk());

        Mockito.verify(reservaService).confirmarReserva(1L);
    }
}
