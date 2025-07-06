package com.gestion.reservas.controller;

import com.gestion.reservas.service.EspacioService;
import com.gestion.reservas.dto.EspacioResponseDTO;
import com.gestion.reservas.dto.TipoEspacioDTO;
import com.gestion.reservas.dto.EstadoEspacioDTO;
import com.gestion.reservas.dto.EquipamientoDTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import com.gestion.reservas.security.JwtAuthenticationFilter;



@WebMvcTest(
        controllers = EspacioController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
public class EspacioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EspacioService espacioService;

    @Autowired
    private ObjectMapper objectMapper;

    private TipoEspacioDTO tipoSalaReuniones;
    private EstadoEspacioDTO estadoActivo;
    private EquipamientoDTO proyector;
    private EquipamientoDTO wifi;
    private EspacioResponseDTO espacio1;
    private EspacioResponseDTO espacio2;

    @BeforeEach
    void setUp() {
        tipoSalaReuniones = TipoEspacioDTO.builder().idTipoEspacio(1L).descripcion("Sala de Reuniones").build();
        estadoActivo = EstadoEspacioDTO.builder().idEstado(1L).descripcion("Activo").build();
        proyector = EquipamientoDTO.builder().idEquipamiento(1L).descripcion("Proyector HDMI").build();
        wifi = EquipamientoDTO.builder().idEquipamiento(5L).descripcion("Wifi").build();

        espacio1 = EspacioResponseDTO.builder()
                .idEspacio(101L)
                .nombre("Sala Alpha")
                .capacidad(10)
                .ubicacion("Piso 3")
                .descripcion("Sala ideal para reuniones pequeñas")
                .imagen("alpha.jpg")
                .tipo(tipoSalaReuniones)
                .estado(estadoActivo)
                .equipamientos(Arrays.asList(proyector, wifi))
                .build();

        espacio2 = EspacioResponseDTO.builder()
                .idEspacio(102L)
                .nombre("Sala Beta")
                .capacidad(20)
                .ubicacion("Piso 5")
                .descripcion("Gran sala para conferencias")
                .imagen("beta.png")
                .tipo(TipoEspacioDTO.builder().idTipoEspacio(2L).descripcion("Auditorio").build())
                .estado(estadoActivo)
                .equipamientos(Collections.singletonList(proyector))
                .build();
    }

    @Test
    void getAll_deberiaRetornarListaDeEspaciosYEstadoOk() throws Exception {
        List<EspacioResponseDTO> espacios = Arrays.asList(espacio1, espacio2);
        when(espacioService.findAll()).thenReturn(espacios);

        mockMvc.perform(get("/api/espacios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].idEspacio").value(espacio1.getIdEspacio()))
                .andExpect(jsonPath("$[0].nombre").value(espacio1.getNombre()))
                .andExpect(jsonPath("$[0].tipo.descripcion").value(espacio1.getTipo().getDescripcion()));

        verify(espacioService, times(1)).findAll();
    }

    @Test
    void getAll_deberiaRetornarListaVaciaSiNoHayEspacios() throws Exception {
        when(espacioService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/espacios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(espacioService, times(1)).findAll();
    }

    @Test
    void getById_existente_deberiaRetornarEspacioYEstadoOk() throws Exception {
        Long id = 101L;
        when(espacioService.findById(id)).thenReturn(espacio1);

        mockMvc.perform(get("/api/espacios/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEspacio").value(espacio1.getIdEspacio()))
                .andExpect(jsonPath("$.nombre").value(espacio1.getNombre()))
                .andExpect(jsonPath("$.capacidad").value(espacio1.getCapacidad()))
                .andExpect(jsonPath("$.ubicacion").value(espacio1.getUbicacion()))
                .andExpect(jsonPath("$.descripcion").value(espacio1.getDescripcion()))
                .andExpect(jsonPath("$.imagen").value(espacio1.getImagen()))
                .andExpect(jsonPath("$.tipo.idTipoEspacio").value(espacio1.getTipo().getIdTipoEspacio()))
                .andExpect(jsonPath("$.tipo.descripcion").value(espacio1.getTipo().getDescripcion()))
                .andExpect(jsonPath("$.estado.idEstado").value(espacio1.getEstado().getIdEstado()))
                .andExpect(jsonPath("$.estado.descripcion").value(espacio1.getEstado().getDescripcion()))
                .andExpect(jsonPath("$.equipamientos.length()").value(2))
                .andExpect(jsonPath("$.equipamientos[0].descripcion").value(proyector.getDescripcion()));


        verify(espacioService, times(1)).findById(id);
    }

    @Test
    void getById_noExistente_deberiaRetornarEstadoNotFound() throws Exception {
        Long id = 999L;
        when(espacioService.findById(id)).thenThrow(new NoSuchElementException("Espacio no encontrado"));

        mockMvc.perform(get("/api/espacios/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(espacioService, times(1)).findById(id);
    }

    @Test
    void create_deberiaCrearEspacioYRetornarEstadoOk() throws Exception {
        MockMultipartFile imagenFile = new MockMultipartFile(
                "imagen", "test.jpg", "image/jpeg", "test image content".getBytes()
        );
        List<Long> equipamientosIds = Arrays.asList(1L, 5L);

        EspacioResponseDTO nuevoEspacio = EspacioResponseDTO.builder()
                .idEspacio(103L)
                .nombre("Nueva Sala")
                .capacidad(15)
                .ubicacion("Piso 2")
                .descripcion("Sala recién creada")
                .imagen("nueva.jpg")
                .tipo(tipoSalaReuniones)
                .estado(estadoActivo)
                .equipamientos(Arrays.asList(proyector, wifi))
                .build();


        when(espacioService.create(
                anyString(), anyInt(), anyString(), anyString(),
                anyLong(), anyLong(), anyList(), any(MockMultipartFile.class) // Usar any(MockMultipartFile.class)
        )).thenReturn(nuevoEspacio);


        mockMvc.perform(multipart("/api/espacios")
                        .file(imagenFile)
                        .param("nombre", "Nueva Sala")
                        .param("capacidad", "15")
                        .param("ubicacion", "Piso 2")
                        .param("descripcion", "Sala recién creada")
                        .param("idTipoEspacio", "1")
                        .param("idEstado", "1")
                        .param("equipamientos", "1", "5") // Parámetros para la lista de equipamientos
                        .contentType(MediaType.MULTIPART_FORM_DATA)) // Asegúrate de que el Content-Type es MULTIPART_FORM_DATA
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEspacio").value(nuevoEspacio.getIdEspacio()))
                .andExpect(jsonPath("$.nombre").value(nuevoEspacio.getNombre()))
                .andExpect(jsonPath("$.capacidad").value(nuevoEspacio.getCapacidad()))
                .andExpect(jsonPath("$.imagen").value(nuevoEspacio.getImagen()));

        // Verificar que el método create fue llamado con los argumentos correctos
        verify(espacioService, times(1)).create(
                eq("Nueva Sala"), eq(15), eq("Piso 2"), eq("Sala recién creada"),
                eq(1L), eq(1L), eq(equipamientosIds), any(MockMultipartFile.class)
        );
    }

    @Test
    void updateEspacio_deberiaActualizarEspacioYRetornarEstadoOk() throws Exception {
        Long id = 101L;
        MockMultipartFile nuevaImagenFile = new MockMultipartFile(
                "imagen", "updated.jpg", "image/jpeg", "updated image content".getBytes()
        );
        List<Long> nuevosEquipamientosIds = Arrays.asList(1L);

        EspacioResponseDTO espacioActualizado = EspacioResponseDTO.builder()
                .idEspacio(id)
                .nombre("Sala Alpha Actualizada")
                .capacidad(12)
                .ubicacion("Piso 3 Actualizado")
                .descripcion("Sala actualizada para reuniones pequeñas")
                .imagen("updated.jpg")
                .tipo(tipoSalaReuniones)
                .estado(estadoActivo)
                .equipamientos(Collections.singletonList(proyector))
                .build();

        when(espacioService.update(
                eq(id),
                anyString(), anyInt(), anyString(), anyString(),
                anyLong(), anyLong(), anyList(), any(MockMultipartFile.class)
        )).thenReturn(espacioActualizado);

        mockMvc.perform(multipart("/api/espacios/{id}", id)
                        .file(nuevaImagenFile)
                        .param("nombre", "Sala Alpha Actualizada")
                        .param("capacidad", "12")
                        .param("ubicacion", "Piso 3 Actualizado")
                        .param("descripcion", "Sala actualizada para reuniones pequeñas")
                        .param("idTipoEspacio", "1")
                        .param("idEstado", "1")
                        .param("equipamientos", "1")
                        .with(request -> { // Hack para simular un PUT con multipart/form-data
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEspacio").value(espacioActualizado.getIdEspacio()))
                .andExpect(jsonPath("$.nombre").value(espacioActualizado.getNombre()))
                .andExpect(jsonPath("$.capacidad").value(espacioActualizado.getCapacidad()))
                .andExpect(jsonPath("$.imagen").value(espacioActualizado.getImagen()));

        verify(espacioService, times(1)).update(
                eq(id),
                eq("Sala Alpha Actualizada"), eq(12), eq("Piso 3 Actualizado"), eq("Sala actualizada para reuniones pequeñas"),
                eq(1L), eq(1L), eq(nuevosEquipamientosIds), any(MockMultipartFile.class)
        );
    }

    @Test
    void delete_deberiaEliminarEspacioYRetornarNoContent() throws Exception {
        Long id = 101L;

        mockMvc.perform(delete("/api/espacios/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // Espera un estado 204 No Content

        verify(espacioService, times(1)).delete(id); // Verifica que el método delete fue llamado
    }

    @Test
    void actualizarEstado_deberiaActualizarEstadoYRetornarNoContent() throws Exception {
        Long id = 101L;
        Long nuevoEstadoId = 2L;

        Map<String, Long> requestBody = Collections.singletonMap("idEstado", nuevoEstadoId);


        mockMvc.perform(post("/api/espacios/{id}/actualizar-estado", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))) // Convierte el mapa a JSON
                .andExpect(status().isNoContent()); // Espera un estado 204 No Content

        verify(espacioService, times(1)).updateEstado(id, nuevoEstadoId); // Verifica la llamada
    }
}