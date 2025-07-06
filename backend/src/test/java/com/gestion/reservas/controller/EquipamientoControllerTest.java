package com.gestion.reservas.controller;

import com.gestion.reservas.dto.EquipamientoDTO;
import com.gestion.reservas.service.EquipamientoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
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
        controllers = EquipamientoController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
public class EquipamientoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EquipamientoService equipamientoService;

    @Autowired
    private ObjectMapper objectMapper;

    private EquipamientoDTO equipamiento1;
    private EquipamientoDTO equipamiento2;

    @BeforeEach
    void setUp() {
        // Inicializa DTOs de ejemplo para los tests
        equipamiento1 = EquipamientoDTO.builder()
                .idEquipamiento(1L)
                .descripcion("Proyector")
                .build();
        equipamiento2 = EquipamientoDTO.builder()
                .idEquipamiento(2L)
                .descripcion("Sistema de audio")
                .build();
    }

    @Test
    void getAllEquipamientos() throws Exception {
        List<EquipamientoDTO> equipamientosEsperados = Arrays.asList(equipamiento1, equipamiento2);

        when(equipamientoService.findAll()).thenReturn(equipamientosEsperados);

        mockMvc.perform(get("/api/equipamientos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Espera un estado 200 OK
                .andExpect(jsonPath("$.length()").value(2)) // Espera 2 elementos en la lista
                .andExpect(jsonPath("$[0].idEquipamiento").value(equipamiento1.getIdEquipamiento()))
                .andExpect(jsonPath("$[0].descripcion").value(equipamiento1.getDescripcion()))
                .andExpect(jsonPath("$[1].idEquipamiento").value(equipamiento2.getIdEquipamiento()))
                .andExpect(jsonPath("$[1].descripcion").value(equipamiento2.getDescripcion()));


        verify(equipamientoService, times(1)).findAll();
    }

    @Test
    void getEquipamientoById() throws Exception {
        Long equipamientoId = 1L;

        when(equipamientoService.findById(equipamientoId)).thenReturn(Optional.of(equipamiento1));

        mockMvc.perform(get("/api/equipamientos/{id}", equipamientoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Espera un estado 200 OK
                .andExpect(jsonPath("$.idEquipamiento").value(equipamiento1.getIdEquipamiento()))
                .andExpect(jsonPath("$.descripcion").value(equipamiento1.getDescripcion()));

                verify(equipamientoService, times(1)).findById(equipamientoId);
    }

    @Test
    void getEquipamientoById_noExistente() throws Exception {
        Long equipamientoId = 99L; // Un ID que no existe

        // Cuando se llame a findById(99L) en el servicio mock, devuelve un Optional vacío
        when(equipamientoService.findById(equipamientoId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/equipamientos/{id}", equipamientoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Espera un estado 404 Not Found


        verify(equipamientoService, times(1)).findById(equipamientoId);
    }
}