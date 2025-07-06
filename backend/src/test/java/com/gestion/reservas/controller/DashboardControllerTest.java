package com.gestion.reservas.controller;

import com.gestion.reservas.dto.DashBoardDTO;
import com.gestion.reservas.dto.KPIDTO;
import com.gestion.reservas.dto.UltimaReservaDTO;
import com.gestion.reservas.dto.TipoSalaDistribucionDTO;

import com.gestion.reservas.service.DashboardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
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
        controllers = DashboardController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
public class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void obtenerDashboard_sinParametros() throws Exception {
       KPIDTO defaultKpi = KPIDTO.builder()
                .totalReservas(100)
                .tasaOcupacion(75)
                .usuariosActivos(40)
                .horasReservadas(500)
                .build();

        List<UltimaReservaDTO> defaultUltimasReservas = Arrays.asList(
                UltimaReservaDTO.builder()
                        .usuario("Juan Perez")
                        .sala("Sala Ejecutiva 1")
                        .fecha(LocalDate.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE))
                        .duracion("2h 30min")
                        .estado("Aprobado")
                        .color("#007bff")
                        .bgcolor("#e0f7fa")
                        .build(),
                UltimaReservaDTO.builder()
                        .usuario("Maria Lopez")
                        .sala("Sala de Formación")
                        .fecha(LocalDate.now().minusDays(1).format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE))
                        .duracion("1h 00min")
                        .estado("Pendiente")
                        .color("#ffc107")
                        .bgcolor("#fff3cd")
                        .build()
        );

        List<Integer> defaultReservasPeriodo = Arrays.asList(10, 12, 15, 8, 20);

        List<TipoSalaDistribucionDTO> defaultTipoSalaDistribucion = Arrays.asList(
                TipoSalaDistribucionDTO.builder().tipo("Conferencia").valor(2).build(),
                TipoSalaDistribucionDTO.builder().tipo("Reuniones").valor(3).build()
        );

        DashBoardDTO expectedDto = DashBoardDTO.builder()
                .kpi(defaultKpi)
                .ultimasReservas(defaultUltimasReservas)
                .reservasPeriodo(defaultReservasPeriodo)
                .tipoSalaDistribucion(defaultTipoSalaDistribucion)
                .build();

        when(dashboardService.obtenerDatosDashboard(
                eq(null), eq(null), eq(null), eq(null)
        )).thenReturn(expectedDto);

        mockMvc.perform(get("/api/dashboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kpi.totalReservas").value(expectedDto.getKpi().getTotalReservas()))
                .andExpect(jsonPath("$.kpi.tasaOcupacion").value(expectedDto.getKpi().getTasaOcupacion()))
                .andExpect(jsonPath("$.kpi.usuariosActivos").value(expectedDto.getKpi().getUsuariosActivos()))
                .andExpect(jsonPath("$.kpi.horasReservadas").value(expectedDto.getKpi().getHorasReservadas()))

                .andExpect(jsonPath("$.ultimasReservas.length()").value(2))
                .andExpect(jsonPath("$.ultimasReservas[0].usuario").value(expectedDto.getUltimasReservas().get(0).getUsuario()))
                .andExpect(jsonPath("$.ultimasReservas[0].sala").value(expectedDto.getUltimasReservas().get(0).getSala()))
                .andExpect(jsonPath("$.ultimasReservas[0].fecha").value(expectedDto.getUltimasReservas().get(0).getFecha()))
                .andExpect(jsonPath("$.ultimasReservas[0].duracion").value(expectedDto.getUltimasReservas().get(0).getDuracion()))
                .andExpect(jsonPath("$.ultimasReservas[0].estado").value(expectedDto.getUltimasReservas().get(0).getEstado()))
                .andExpect(jsonPath("$.ultimasReservas[0].color").value(expectedDto.getUltimasReservas().get(0).getColor()))
                .andExpect(jsonPath("$.ultimasReservas[0].bgcolor").value(expectedDto.getUltimasReservas().get(0).getBgcolor()))

                .andExpect(jsonPath("$.reservasPeriodo[0]").value(expectedDto.getReservasPeriodo().get(0)))
                .andExpect(jsonPath("$.reservasPeriodo[1]").value(expectedDto.getReservasPeriodo().get(1)))
                .andExpect(jsonPath("$.reservasPeriodo.length()").value(5))

                .andExpect(jsonPath("$.tipoSalaDistribucion.length()").value(2))
                .andExpect(jsonPath("$.tipoSalaDistribucion[0].tipo").value(expectedDto.getTipoSalaDistribucion().get(0).getTipo()))
                .andExpect(jsonPath("$.tipoSalaDistribucion[0].valor").value(expectedDto.getTipoSalaDistribucion().get(0).getValor()));

        verify(dashboardService, times(1)).obtenerDatosDashboard(eq(null), eq(null), eq(null), eq(null));
    }

    @Test
    void obtenerDashboard_conTodosLosParametros() throws Exception {
        LocalDate fechaInicio = LocalDate.of(2025, 1, 1);
        LocalDate fechaFin = LocalDate.of(2025, 12, 31);
        Long tipoSala = 1L;
        Long estado = 2L;

        KPIDTO filteredKpi = KPIDTO.builder()
                .totalReservas(50)
                .tasaOcupacion(80)
                .usuariosActivos(25)
                .horasReservadas(300)
                .build();

        List<UltimaReservaDTO> filteredUltimasReservas = Collections.singletonList(
                UltimaReservaDTO.builder()
                        .usuario("Carlos Ruiz")
                        .sala("Sala de Conferencias Grande")
                        .fecha(LocalDate.of(2025, 3, 15).format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE))
                        .duracion("3h 00min")
                        .estado("Aprobado")
                        .color("#28a745")
                        .bgcolor("#d4edda")
                        .build()
        );

        List<Integer> filteredReservasPeriodo = Arrays.asList(5, 7, 3);

        List<TipoSalaDistribucionDTO> filteredTipoSalaDistribucion = Collections.singletonList(
                TipoSalaDistribucionDTO.builder().tipo("Conferencia").valor(1).build()
        );

        DashBoardDTO filteredDto = DashBoardDTO.builder()
                .kpi(filteredKpi)
                .ultimasReservas(filteredUltimasReservas)
                .reservasPeriodo(filteredReservasPeriodo)
                .tipoSalaDistribucion(filteredTipoSalaDistribucion)
                .build();

        when(dashboardService.obtenerDatosDashboard(
                eq(fechaInicio), eq(fechaFin), eq(tipoSala), eq(estado)
        )).thenReturn(filteredDto);

        mockMvc.perform(get("/api/dashboard")
                        .param("fechaInicio", fechaInicio.toString())
                        .param("fechaFin", fechaFin.toString())
                        .param("tipoSala", tipoSala.toString())
                        .param("estado", estado.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kpi.totalReservas").value(filteredDto.getKpi().getTotalReservas()))
                .andExpect(jsonPath("$.kpi.tasaOcupacion").value(filteredDto.getKpi().getTasaOcupacion()))
                .andExpect(jsonPath("$.kpi.usuariosActivos").value(filteredDto.getKpi().getUsuariosActivos()))
                .andExpect(jsonPath("$.kpi.horasReservadas").value(filteredDto.getKpi().getHorasReservadas()))

                .andExpect(jsonPath("$.ultimasReservas.length()").value(1))
                .andExpect(jsonPath("$.ultimasReservas[0].usuario").value(filteredDto.getUltimasReservas().get(0).getUsuario()))
                .andExpect(jsonPath("$.ultimasReservas[0].sala").value(filteredDto.getUltimasReservas().get(0).getSala()))
                .andExpect(jsonPath("$.ultimasReservas[0].fecha").value(filteredDto.getUltimasReservas().get(0).getFecha()))
                .andExpect(jsonPath("$.ultimasReservas[0].estado").value(filteredDto.getUltimasReservas().get(0).getEstado()))
                .andExpect(jsonPath("$.reservasPeriodo.length()").value(3))
                .andExpect(jsonPath("$.tipoSalaDistribucion.length()").value(1))
                .andExpect(jsonPath("$.tipoSalaDistribucion[0].tipo").value(filteredDto.getTipoSalaDistribucion().get(0).getTipo()))
                .andExpect(jsonPath("$.tipoSalaDistribucion[0].valor").value(filteredDto.getTipoSalaDistribucion().get(0).getValor()));

        verify(dashboardService, times(1)).obtenerDatosDashboard(
                eq(fechaInicio), eq(fechaFin), eq(tipoSala), eq(estado)
        );
    }

    @Test
    void obtenerDashboard_conSoloFechas() throws Exception {
        LocalDate fechaInicio = LocalDate.of(2025, 6, 1);
        LocalDate fechaFin = LocalDate.of(2025, 6, 30);


        KPIDTO monthlyKpi = KPIDTO.builder()
                .totalReservas(30)
                .tasaOcupacion(60)
                .usuariosActivos(10)
                .horasReservadas(150)
                .build();

        DashBoardDTO monthlyDto = DashBoardDTO.builder()
                .kpi(monthlyKpi)
                .ultimasReservas(Collections.emptyList())
                .reservasPeriodo(Arrays.asList(3, 4, 5))
                .tipoSalaDistribucion(Collections.emptyList())
                .build();

        when(dashboardService.obtenerDatosDashboard(
                eq(fechaInicio), eq(fechaFin), eq(null), eq(null)
        )).thenReturn(monthlyDto);

        mockMvc.perform(get("/api/dashboard")
                        .param("fechaInicio", fechaInicio.toString())
                        .param("fechaFin", fechaFin.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kpi.totalReservas").value(monthlyDto.getKpi().getTotalReservas()))
                .andExpect(jsonPath("$.kpi.tasaOcupacion").value(monthlyDto.getKpi().getTasaOcupacion()))
                .andExpect(jsonPath("$.kpi.usuariosActivos").value(monthlyDto.getKpi().getUsuariosActivos()))
                .andExpect(jsonPath("$.kpi.horasReservadas").value(monthlyDto.getKpi().getHorasReservadas()))
                .andExpect(jsonPath("$.ultimasReservas.length()").value(0))
                .andExpect(jsonPath("$.reservasPeriodo.length()").value(3))
                .andExpect(jsonPath("$.tipoSalaDistribucion.length()").value(0));

        verify(dashboardService, times(1)).obtenerDatosDashboard(
                eq(fechaInicio), eq(fechaFin), eq(null), eq(null)
        );
    }
}