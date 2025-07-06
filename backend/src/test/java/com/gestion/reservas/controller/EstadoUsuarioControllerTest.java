package com.gestion.reservas.controller;

import com.gestion.reservas.entity.EstadoUsuario;
import com.gestion.reservas.service.EstadoUsuarioService;

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
        controllers = EstadoUsuarioController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
public class EstadoUsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EstadoUsuarioService estadoUsuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    private EstadoUsuario estadoActivo;
    private EstadoUsuario estadoInactivo;


    @BeforeEach
    void setUp() {

        estadoActivo = EstadoUsuario.builder()
                .idEstado(1L)
                .descripcion("Activo")
                .build();
        estadoInactivo = EstadoUsuario.builder()
                .idEstado(2L)
                .descripcion("Inactivo")
                .build();

    }

    @Test
    void getAllEstadosUsuario_ListaDeEstadosYEstadoOk() throws Exception {
        List<EstadoUsuario> estadosEsperados = Arrays.asList(estadoActivo, estadoInactivo);

        // Cuando se llame a findAll() en el servicio mock, devuelve nuestra lista
        when(estadoUsuarioService.findAll()).thenReturn(estadosEsperados);

        mockMvc.perform(get("/api/estadosusuario")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Espera un estado 200 OK
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].idEstado").value(estadoActivo.getIdEstado()))
                .andExpect(jsonPath("$[0].descripcion").value(estadoActivo.getDescripcion()))
                .andExpect(jsonPath("$[1].idEstado").value(estadoInactivo.getIdEstado()))
                .andExpect(jsonPath("$[1].descripcion").value(estadoInactivo.getDescripcion()));


        // Verifica que findAll() fue llamado exactamente una vez en el servicio
        verify(estadoUsuarioService, times(1)).findAll();
    }

    @Test
    void getAllEstadosUsuario_ListaVaciaSiNoHayEstados() throws Exception {
        when(estadoUsuarioService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/estadosusuario")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(estadoUsuarioService, times(1)).findAll();
    }

    @Test
    void getEstadoUsuarioById_existente_() throws Exception {
        Long id = 1L; // ID de estado Activo

        // Cuando se llame a findById(1L) en el servicio mock, devuelve un Optional con estadoActivo
        when(estadoUsuarioService.findById(id)).thenReturn(Optional.of(estadoActivo));

        mockMvc.perform(get("/api/estadosusuario/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Espera un estado 200 OK
                .andExpect(jsonPath("$.idEstado").value(estadoActivo.getIdEstado()))
                .andExpect(jsonPath("$.descripcion").value(estadoActivo.getDescripcion()));

        // Verifica que findById() fue llamado exactamente una vez con el ID correcto
        verify(estadoUsuarioService, times(1)).findById(id);
    }

    @Test
    void getEstadoUsuarioById_noExistente() throws Exception {
        Long id = 99L; // Un ID que no existe

        // Cuando se llame a findById(99L) en el servicio mock, devuelve un Optional vacío
        when(estadoUsuarioService.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/estadosusuario/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Espera un estado 404 Not Found

        // Verifica que findById() fue llamado exactamente una vez con el ID correcto
        verify(estadoUsuarioService, times(1)).findById(id);
    }
}