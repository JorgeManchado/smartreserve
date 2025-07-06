package com.gestion.reservas.controller;

import com.gestion.reservas.dto.NotificacionDTO;
import com.gestion.reservas.security.JwtAuthenticationFilter;
import com.gestion.reservas.service.NotificacionServiceImpl;
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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = NotificacionController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
class NotificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificacionServiceImpl notificacionService;

    @Test
    void testGetNotificacionesUsuario() throws Exception {
        NotificacionDTO noti = new NotificacionDTO(
                1L,
                "Reserva confirmada",
                false,
                LocalDateTime.now(),
                100L,
                "Carlos"
        );

        Mockito.when(notificacionService.findByUsuarioId(100L)).thenReturn(List.of());
        Mockito.when(notificacionService.toDtoList(Mockito.anyList())).thenReturn(List.of(noti));

        mockMvc.perform(get("/api/notificaciones/usuario/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].mensaje").value("Reserva confirmada"))
                .andExpect(jsonPath("$[0].nombreUsuario").value("Carlos"));
    }

    @Test
    void testMarcarComoLeida() throws Exception {
        mockMvc.perform(put("/api/notificaciones/1/leida")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Mockito.verify(notificacionService).marcarComoLeida(1L);
    }
}
