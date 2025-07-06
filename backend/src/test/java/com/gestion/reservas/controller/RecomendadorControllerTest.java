package com.gestion.reservas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion.reservas.dto.RecomendacionRequestDTO;
import com.gestion.reservas.dto.RecomendacionResponseDTO;
import com.gestion.reservas.security.JwtAuthenticationFilter;
import com.gestion.reservas.service.RecomendadorService;
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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(
        controllers = RecomendadorController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
class RecomendadorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecomendadorService recomendadorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRecomendar_success() throws Exception {
        RecomendacionResponseDTO mockResponse = new RecomendacionResponseDTO();
        mockResponse.setUsuario_id(1L);
        mockResponse.setNombre_usuario("Carlos");
        mockResponse.setDia_semana(3);
        mockResponse.setHora(10);
        mockResponse.setRecomendaciones(Collections.emptyList());

        Mockito.when(recomendadorService.obtenerRecomendaciones(eq(1L), eq(3), eq(10)))
                .thenReturn(mockResponse);

        RecomendacionRequestDTO request = new RecomendacionRequestDTO();
        request.setUsuarioId(1L);
        request.setDiaSemana(3);
        request.setHora(10);

        mockMvc.perform(post("/api/recomendador")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario_id").value(1))
                .andExpect(jsonPath("$.nombre_usuario").value("Carlos"));
    }

    @Test
    void testRecomendar_error() throws Exception {
        Mockito.when(recomendadorService.obtenerRecomendaciones(anyLong(), anyInt(), anyInt()))
                .thenThrow(new RuntimeException("Error interno"));

        RecomendacionRequestDTO request = new RecomendacionRequestDTO();
        request.setUsuarioId(1L);
        request.setDiaSemana(2);
        request.setHora(12);

        mockMvc.perform(post("/api/recomendador")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testEntrenarModelo_success() throws Exception {
        Mockito.when(recomendadorService.entrenarModelo())
                .thenReturn("Modelo entrenado correctamente");

        mockMvc.perform(post("/api/recomendador/entrenar"))
                .andExpect(status().isOk())
                .andExpect(content().string("Modelo entrenado correctamente"));
    }

    @Test
    void testEntrenarModelo_error() throws Exception {
        Mockito.when(recomendadorService.entrenarModelo())
                .thenThrow(new RuntimeException("Fallo al entrenar"));

        mockMvc.perform(post("/api/recomendador/entrenar"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error: Fallo al entrenar"));
    }
}
