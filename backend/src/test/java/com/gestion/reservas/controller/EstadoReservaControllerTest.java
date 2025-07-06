package com.gestion.reservas.controller;

import com.gestion.reservas.entity.EstadoReserva;
import com.gestion.reservas.service.EstadoReservaService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import com.gestion.reservas.security.JwtAuthenticationFilter;


@WebMvcTest(
        controllers = EstadoReservaController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
public class EstadoReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EstadoReservaService estadoReservaService;

    @Autowired
    private ObjectMapper objectMapper;

    private EstadoReserva estadoPendiente;
    private EstadoReserva estadoAprobado;
    private EstadoReserva estadoAnulado;

    @BeforeEach
    void setUp() {

        estadoPendiente = EstadoReserva.builder()
                .idEstado(1L)
                .descripcion("Pendiente")
                .color("#FFC107")
                .bgcolor("#FFF3CD")
                .build();
        estadoAprobado = EstadoReserva.builder()
                .idEstado(2L)
                .descripcion("Aprobado")
                .color("#28A745")
                .bgcolor("#D4EDDA")
                .build();
        estadoAnulado = EstadoReserva.builder()
                .idEstado(3L)
                .descripcion("Anulado")
                .color("#DC3545")
                .bgcolor("#F8D7DA")
                .build();
    }

    @Test
    void getAllEstadosReserva_ListaDeEstadosYEstadoOk() throws Exception {
        List<EstadoReserva> estadosEsperados = Arrays.asList(estadoPendiente, estadoAprobado, estadoAnulado);

        when(estadoReservaService.findAll()).thenReturn(estadosEsperados);

        mockMvc.perform(get("/api/estadosreservas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Espera un estado 200 OK
                .andExpect(jsonPath("$.length()").value(3)) // Espera 3 elementos en la lista
                .andExpect(jsonPath("$[0].idEstado").value(estadoPendiente.getIdEstado()))
                .andExpect(jsonPath("$[0].descripcion").value(estadoPendiente.getDescripcion()))
                .andExpect(jsonPath("$[0].color").value(estadoPendiente.getColor()))
                .andExpect(jsonPath("$[0].bgcolor").value(estadoPendiente.getBgcolor()))
                .andExpect(jsonPath("$[1].idEstado").value(estadoAprobado.getIdEstado()))
                .andExpect(jsonPath("$[1].descripcion").value(estadoAprobado.getDescripcion()))
                .andExpect(jsonPath("$[2].idEstado").value(estadoAnulado.getIdEstado()))
                .andExpect(jsonPath("$[2].descripcion").value(estadoAnulado.getDescripcion()));


        verify(estadoReservaService, times(1)).findAll();
    }

    @Test
    void getAllEstadosReserva_ListaVaciaSiNoHayEstados() throws Exception {
        when(estadoReservaService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/estadosreservas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(estadoReservaService, times(1)).findAll();
    }

    @Test
    void getEstadoReservaById_existente() throws Exception {
        Long id = 1L; // ID de estado Pendiente

        // Cuando se llame a findById(1L) en el servicio mock, devuelve un Optional con estadoPendiente
        when(estadoReservaService.findById(id)).thenReturn(Optional.of(estadoPendiente));

        mockMvc.perform(get("/api/estadosreservas/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Espera un estado 200 OK
                .andExpect(jsonPath("$.idEstado").value(estadoPendiente.getIdEstado()))
                .andExpect(jsonPath("$.descripcion").value(estadoPendiente.getDescripcion()))
                .andExpect(jsonPath("$.color").value(estadoPendiente.getColor()))
                .andExpect(jsonPath("$.bgcolor").value(estadoPendiente.getBgcolor()));

        // Verifica que findById() fue llamado exactamente una vez con el ID correcto
        verify(estadoReservaService, times(1)).findById(id);
    }

    @Test
    void getEstadoReservaById_noExistente() throws Exception {
        Long id = 99L; // Un ID que no existe

        // Cuando se llame a findById(99L) en el servicio mock, devuelve un Optional vacío
        when(estadoReservaService.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/estadosreservas/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Espera un estado 404 Not Found

        // Verifica que findById() fue llamado exactamente una vez con el ID correcto
        verify(estadoReservaService, times(1)).findById(id);
    }
}