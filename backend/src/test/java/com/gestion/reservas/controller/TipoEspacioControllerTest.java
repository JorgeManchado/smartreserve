package com.gestion.reservas.controller;

import com.gestion.reservas.entity.TipoEspacio;
import com.gestion.reservas.security.JwtAuthenticationFilter;
import com.gestion.reservas.service.TipoEspacioService;
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

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = TipoEspacioController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
class TipoEspacioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TipoEspacioService tipoEspacioService;

    @Test
    void testGetAllTiposEspacio() throws Exception {
        List<TipoEspacio> tipos = List.of(
                new TipoEspacio(1L, "Sala de reuniones"),
                new TipoEspacio(2L, "Sala de conferencias")
        );

        Mockito.when(tipoEspacioService.findAll()).thenReturn(tipos);

        mockMvc.perform(get("/api/tiposespacios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].descripcion").value("Sala de reuniones"))
                .andExpect(jsonPath("$[1].descripcion").value("Sala de conferencias"));
    }

    @Test
    void testGetTipoEspacioById_found() throws Exception {
        TipoEspacio tipo = new TipoEspacio(1L, "Auditorio");

        Mockito.when(tipoEspacioService.findById(1L)).thenReturn(Optional.of(tipo));

        mockMvc.perform(get("/api/tiposespacios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTipoEspacio").value(1))
                .andExpect(jsonPath("$.descripcion").value("Auditorio"));
    }

    @Test
    void testGetTipoEspacioById_notFound() throws Exception {
        Mockito.when(tipoEspacioService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tiposespacios/999"))
                .andExpect(status().isNotFound());
    }
}
