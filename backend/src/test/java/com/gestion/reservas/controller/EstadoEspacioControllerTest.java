package com.gestion.reservas.controller;

import com.gestion.reservas.entity.EstadoEspacio;
import com.gestion.reservas.service.EstadoEspacioService;

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
        controllers = EstadoEspacioController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
public class EstadoEspacioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EstadoEspacioService estadoEspacioService;

    @Autowired
    private ObjectMapper objectMapper;

    private EstadoEspacio estadoActivo;
    private EstadoEspacio estadoInactivo;

    @BeforeEach
    void setUp() {

        estadoActivo = EstadoEspacio.builder()
                .idEstado(1L)
                .descripcion("Activo")
                .build();
        estadoInactivo = EstadoEspacio.builder()
                .idEstado(2L)
                .descripcion("Inactivo")
                .build();
    }

    @Test
    void getAllEstadosEspacio() throws Exception {
        List<EstadoEspacio> estadosEsperados = Arrays.asList(estadoActivo, estadoInactivo);

        // Cuando se llame a findAll() en el servicio mock, devuelve nuestra lista
        when(estadoEspacioService.findAll()).thenReturn(estadosEsperados);

        mockMvc.perform(get("/api/estadosespacios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Espera un estado 200 OK
                .andExpect(jsonPath("$.length()").value(2)) // Espera 2 elementos en la lista
                .andExpect(jsonPath("$[0].idEstado").value(estadoActivo.getIdEstado()))
                .andExpect(jsonPath("$[0].descripcion").value(estadoActivo.getDescripcion()))
                .andExpect(jsonPath("$[1].idEstado").value(estadoInactivo.getIdEstado()))
                .andExpect(jsonPath("$[1].descripcion").value(estadoInactivo.getDescripcion()));

        // Verifica que findAll() fue llamado exactamente una vez en el servicio
        verify(estadoEspacioService, times(1)).findAll();
    }

    @Test
    void getAllEstadosEspacio_ListaVaciaSiNoHayEstados() throws Exception {
        when(estadoEspacioService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/estadosespacios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(estadoEspacioService, times(1)).findAll();
    }

    @Test
    void getEstadoEspacioById() throws Exception {
        Long id = 1L;

        // Cuando se llame a findById(1L) en el servicio mock, devuelve un Optional con estadoActivo
        when(estadoEspacioService.findById(id)).thenReturn(Optional.of(estadoActivo));

        mockMvc.perform(get("/api/estadosespacios/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Espera un estado 200 OK
                .andExpect(jsonPath("$.idEstado").value(estadoActivo.getIdEstado()))
                .andExpect(jsonPath("$.descripcion").value(estadoActivo.getDescripcion()));

        // Verifica que findById() fue llamado exactamente una vez con el ID correcto
        verify(estadoEspacioService, times(1)).findById(id);
    }

    @Test
    void getEstadoEspacioById_noExistente() throws Exception {
        Long id = 99L; // Un ID que no existe

        // Cuando se llame a findById(99L) en el servicio mock, devuelve un Optional vacío
        when(estadoEspacioService.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/estadosespacios/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Espera un estado 404 Not Found

        // Verifica que findById() fue llamado exactamente una vez con el ID correcto
        verify(estadoEspacioService, times(1)).findById(id);
    }
}