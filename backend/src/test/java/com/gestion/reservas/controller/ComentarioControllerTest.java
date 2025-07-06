package com.gestion.reservas.controller;

import com.gestion.reservas.dto.ComentarioDTO;
import com.gestion.reservas.service.ComentarioService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import com.gestion.reservas.security.JwtAuthenticationFilter;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;


@WebMvcTest(
        controllers = ComentarioController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
public class ComentarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ComentarioService comentarioService;

    @Autowired
    private ObjectMapper objectMapper;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private ComentarioDTO comentarioDB1;
    private ComentarioDTO comentarioDB2;
    private ComentarioDTO comentarioDB3;

    @BeforeEach
    void setUp() {
        // Los códigos de estado y descripciones se toman de la tabla 'estados_comentario'
        // 1: Pendiente
        // 2: Aprobado
        // 3: Anulado

        comentarioDB1 = new ComentarioDTO(
                34L, // idComentario
                "Muy buena ubicación y fácil acceso. Me gustó la...",
                5, // valoracion
                LocalDateTime.parse("2025-05-27 11:13:03", formatter),
                "Aprobado",
                2L,
                "Teresa",
                18L,
                132L
        );

        comentarioDB2 = new ComentarioDTO(
                36L,
                "La sala cumplía con las medidas acordadas",
                4, // valoracion
                LocalDateTime.parse("2025-06-01 21:09:49", formatter),
                "Aprobado",
                2L,
                "Mariano",
                21L,
                140L
        );

        comentarioDB3 = new ComentarioDTO(
                38L,
                "La sala no cumplía las medidas de eficiencia ene...",
                2,
                LocalDateTime.parse("2025-06-12 15:56:45", formatter),
                "Pendiente",
                1L,
                "Marcos",
                20L,
                131L
        );
    }

    @Test
    void listarComentarios() throws Exception {
        List<ComentarioDTO> comentariosEsperados = Arrays.asList(comentarioDB1, comentarioDB2, comentarioDB3);
        when(comentarioService.listarComentarios()).thenReturn(comentariosEsperados);

        mockMvc.perform(get("/api/comentarios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].idComentario").value(comentarioDB1.getIdComentario()))
                .andExpect(jsonPath("$[0].texto").value(comentarioDB1.getTexto()))
                .andExpect(jsonPath("$[0].valoracion").value(comentarioDB1.getValoracion()))
                .andExpect(jsonPath("$[0].estadoDescripcion").value(comentarioDB1.getEstadoDescripcion()))
                .andExpect(jsonPath("$[0].idEstado").value(comentarioDB1.getIdEstado()))
                .andExpect(jsonPath("$[0].nombreUsuario").value(comentarioDB1.getNombreUsuario()))
                .andExpect(jsonPath("$[0].idUsuario").value(comentarioDB1.getIdUsuario()))
                .andExpect(jsonPath("$[0].idReserva").value(comentarioDB1.getIdReserva()))
                .andExpect(jsonPath("$[1].idComentario").value(comentarioDB2.getIdComentario()))
                .andExpect(jsonPath("$[1].texto").value(comentarioDB2.getTexto()))
                .andExpect(jsonPath("$[1].valoracion").value(comentarioDB2.getValoracion()))
                .andExpect(jsonPath("$[1].estadoDescripcion").value(comentarioDB2.getEstadoDescripcion()))
                .andExpect(jsonPath("$[1].idEstado").value(comentarioDB2.getIdEstado()))
                .andExpect(jsonPath("$[1].nombreUsuario").value(comentarioDB2.getNombreUsuario()))
                .andExpect(jsonPath("$[1].idUsuario").value(comentarioDB2.getIdUsuario()))
                .andExpect(jsonPath("$[1].idReserva").value(comentarioDB2.getIdReserva()))
                .andExpect(jsonPath("$[2].idComentario").value(comentarioDB3.getIdComentario()))
                .andExpect(jsonPath("$[2].texto").value(comentarioDB3.getTexto()))
                .andExpect(jsonPath("$[2].valoracion").value(comentarioDB3.getValoracion()))
                .andExpect(jsonPath("$[2].estadoDescripcion").value(comentarioDB3.getEstadoDescripcion()))
                .andExpect(jsonPath("$[2].idEstado").value(comentarioDB3.getIdEstado()))
                .andExpect(jsonPath("$[2].nombreUsuario").value(comentarioDB3.getNombreUsuario()))
                .andExpect(jsonPath("$[2].idUsuario").value(comentarioDB3.getIdUsuario()))
                .andExpect(jsonPath("$[2].idReserva").value(comentarioDB3.getIdReserva()));

        verify(comentarioService, times(1)).listarComentarios();
    }

    @Test
    void aprobarComentario() throws Exception {
        Long comentarioIdParaAprobar = 38L;

        mockMvc.perform(put("/api/comentarios/aprobar/{id}", comentarioIdParaAprobar)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(comentarioService, times(1)).aprobarComentario(comentarioIdParaAprobar);
    }

    @Test
    void anularComentario() throws Exception {
        Long comentarioIdParaAnular = 34L;

        mockMvc.perform(put("/api/comentarios/anular/{id}", comentarioIdParaAnular)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(comentarioService, times(1)).anularComentario(comentarioIdParaAnular);
    }

    @Test
    void crearComentario() throws Exception {

        ComentarioDTO nuevoComentarioInput = new ComentarioDTO(
                null,
                "Excelente servicio y atención al cliente.",
                5, // valoracion
                LocalDateTime.now(),
                "Pendiente",
                1L,
                "Laura",
                4L,
                22L
        );

        ComentarioDTO nuevoComentarioSalida = new ComentarioDTO(
                100L,
                "Excelente servicio y atención al cliente.",
                5,
                LocalDateTime.parse("2025-07-01 10:00:00", formatter),
                "Pendiente",
                1L,
                "Laura",
                4L,
                22L
        );

        when(comentarioService.crearComentario(any(ComentarioDTO.class))).thenReturn(nuevoComentarioSalida);

        mockMvc.perform(post("/api/comentarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoComentarioInput)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idComentario").value(nuevoComentarioSalida.getIdComentario()))
                .andExpect(jsonPath("$.texto").value(nuevoComentarioSalida.getTexto()))
                .andExpect(jsonPath("$.valoracion").value(nuevoComentarioSalida.getValoracion()))
                .andExpect(jsonPath("$.estadoDescripcion").value(nuevoComentarioSalida.getEstadoDescripcion()))
                .andExpect(jsonPath("$.idEstado").value(nuevoComentarioSalida.getIdEstado()))
                .andExpect(jsonPath("$.idUsuario").value(nuevoComentarioSalida.getIdUsuario()))
                .andExpect(jsonPath("$.nombreUsuario").value(nuevoComentarioSalida.getNombreUsuario()))
                .andExpect(jsonPath("$.idReserva").value(nuevoComentarioSalida.getIdReserva()));

        verify(comentarioService, times(1)).crearComentario(any(ComentarioDTO.class));
    }
}