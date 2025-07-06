package com.gestion.reservas.controller;

import com.gestion.reservas.service.EspacioComentariosService;
import com.gestion.reservas.dto.EspacioComentariosDTO;
import com.gestion.reservas.dto.ComentarioDTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

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
        controllers = EspacioComentariosController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
public class EspacioComentariosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EspacioComentariosService espacioComentariosService;

    @Autowired
    private ObjectMapper objectMapper;

    private ComentarioDTO comentario1;
    private ComentarioDTO comentario2;
    private EspacioComentariosDTO espacioComentarios1;
    private EspacioComentariosDTO espacioComentarios2;

    @BeforeEach
    void setUp() {
        DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        comentario1 = ComentarioDTO.builder()
                .idComentario(34L)
                .texto("Muy buena ubicación y fácil acceso. Me gustó la sala.")
                .valoracion(5)
                .fecha(LocalDateTime.of(2025, 5, 27, 11, 13, 3))
                .estadoDescripcion("Aprobado")
                .idEstado(2L)
                .nombreUsuario("Jorge Manchado")
                .idUsuario(5L)
                .idReserva(132L)
                .build();

        comentario2 = ComentarioDTO.builder()
                .idComentario(35L)
                .texto("Todo bien, aunque la conexión Wi-Fi fue algo inestable.")
                .valoracion(3)
                .fecha(LocalDateTime.of(2025, 5, 27, 11, 13, 40))
                .estadoDescripcion("Aprobado")
                .idEstado(2L)
                .nombreUsuario("Mariano")
                .idUsuario(21L)
                .idReserva(128L)
                .build();

        espacioComentarios1 = EspacioComentariosDTO.builder()
                .idEspacio(1L)
                .nombre("Sala Ejecutiva 1")
                .imagen("imagen_sala_ejecutiva.jpg")
                .valoracionPromedio(4.5)
                .cantidadResenas(10L)
                .comentarios(Arrays.asList(comentario1, comentario2))
                .build();

        espacioComentarios2 = EspacioComentariosDTO.builder()
                .idEspacio(2L)
                .nombre("Sala de Conferencias")
                .imagen("imagen_sala_conferencias.png")
                .valoracionPromedio(3.8)
                .cantidadResenas(5L)
                .comentarios(Arrays.asList(
                        ComentarioDTO.builder()
                                .idComentario(36L)
                                .texto("La sala cumplía con las medidas acordadas")
                                .valoracion(4)
                                .fecha(LocalDateTime.of(2025, 6, 1, 21, 9, 49))
                                .estadoDescripcion("Aprobado")
                                .idEstado(2L)
                                .nombreUsuario("Ainhoa")
                                .idUsuario(17L)
                                .idReserva(140L)
                                .build()
                ))
                .build();
    }

    @Test
    void obtenerEspaciosConComentarios() throws Exception {
        List<EspacioComentariosDTO> resumenEsperado = Arrays.asList(espacioComentarios1, espacioComentarios2);

        when(espacioComentariosService.obtenerResumenComentarios()).thenReturn(resumenEsperado);

        mockMvc.perform(get("/api/espacios/comentarios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))

                .andExpect(jsonPath("$[0].idEspacio").value(espacioComentarios1.getIdEspacio()))
                .andExpect(jsonPath("$[0].nombre").value(espacioComentarios1.getNombre())) // 'nombre' en lugar de 'nombreEspacio'
                .andExpect(jsonPath("$[0].imagen").value(espacioComentarios1.getImagen())) // Nuevo campo
                .andExpect(jsonPath("$[0].valoracionPromedio").value(espacioComentarios1.getValoracionPromedio())) // Nuevo campo
                .andExpect(jsonPath("$[0].cantidadResenas").value(espacioComentarios1.getCantidadResenas())) // Nuevo campo
                .andExpect(jsonPath("$[0].comentarios.length()").value(2))
                .andExpect(jsonPath("$[0].comentarios[0].idComentario").value(comentario1.getIdComentario()))
                .andExpect(jsonPath("$[0].comentarios[0].texto").value(comentario1.getTexto()))
                .andExpect(jsonPath("$[0].comentarios[0].valoracion").value(comentario1.getValoracion()))
                .andExpect(jsonPath("$[0].comentarios[0].fecha").value(comentario1.getFecha().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$[0].comentarios[0].estadoDescripcion").value(comentario1.getEstadoDescripcion()))
                .andExpect(jsonPath("$[0].comentarios[0].idEstado").value(comentario1.getIdEstado()))
                .andExpect(jsonPath("$[0].comentarios[0].nombreUsuario").value(comentario1.getNombreUsuario()))
                .andExpect(jsonPath("$[0].comentarios[0].idUsuario").value(comentario1.getIdUsuario()))
                .andExpect(jsonPath("$[0].comentarios[0].idReserva").value(comentario1.getIdReserva()))

                .andExpect(jsonPath("$[0].comentarios[1].idComentario").value(comentario2.getIdComentario()))
                .andExpect(jsonPath("$[0].comentarios[1].texto").value(comentario2.getTexto()))
                .andExpect(jsonPath("$[0].comentarios[1].valoracion").value(comentario2.getValoracion()))
                .andExpect(jsonPath("$[0].comentarios[1].fecha").value(comentario2.getFecha().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$[0].comentarios[1].estadoDescripcion").value(comentario2.getEstadoDescripcion()))
                .andExpect(jsonPath("$[0].comentarios[1].idEstado").value(comentario2.getIdEstado()))
                .andExpect(jsonPath("$[0].comentarios[1].nombreUsuario").value(comentario2.getNombreUsuario()))
                .andExpect(jsonPath("$[0].comentarios[1].idUsuario").value(comentario2.getIdUsuario()))
                .andExpect(jsonPath("$[0].comentarios[1].idReserva").value(comentario2.getIdReserva()))

                .andExpect(jsonPath("$[1].idEspacio").value(espacioComentarios2.getIdEspacio()))
                .andExpect(jsonPath("$[1].nombre").value(espacioComentarios2.getNombre()))
                .andExpect(jsonPath("$[1].imagen").value(espacioComentarios2.getImagen()))
                .andExpect(jsonPath("$[1].valoracionPromedio").value(espacioComentarios2.getValoracionPromedio()))
                .andExpect(jsonPath("$[1].cantidadResenas").value(espacioComentarios2.getCantidadResenas()))
                .andExpect(jsonPath("$[1].comentarios.length()").value(1))
                .andExpect(jsonPath("$[1].comentarios[0].idComentario").value(espacioComentarios2.getComentarios().get(0).getIdComentario()))
                .andExpect(jsonPath("$[1].comentarios[0].texto").value(espacioComentarios2.getComentarios().get(0).getTexto()))
                .andExpect(jsonPath("$[1].comentarios[0].valoracion").value(espacioComentarios2.getComentarios().get(0).getValoracion()))
                .andExpect(jsonPath("$[1].comentarios[0].fecha").value(espacioComentarios2.getComentarios().get(0).getFecha().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$[1].comentarios[0].estadoDescripcion").value(espacioComentarios2.getComentarios().get(0).getEstadoDescripcion()))
                .andExpect(jsonPath("$[1].comentarios[0].idEstado").value(espacioComentarios2.getComentarios().get(0).getIdEstado()))
                .andExpect(jsonPath("$[1].comentarios[0].nombreUsuario").value(espacioComentarios2.getComentarios().get(0).getNombreUsuario()))
                .andExpect(jsonPath("$[1].comentarios[0].idUsuario").value(espacioComentarios2.getComentarios().get(0).getIdUsuario()))
                .andExpect(jsonPath("$[1].comentarios[0].idReserva").value(espacioComentarios2.getComentarios().get(0).getIdReserva()));

        verify(espacioComentariosService, times(1)).obtenerResumenComentarios();
    }
}