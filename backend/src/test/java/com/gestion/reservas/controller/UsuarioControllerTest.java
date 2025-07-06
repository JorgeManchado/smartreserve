package com.gestion.reservas.controller;

import com.gestion.reservas.dto.UsuarioRequestDTO;
import com.gestion.reservas.dto.UsuarioResponseDTO;
import com.gestion.reservas.entity.Usuario;
import com.gestion.reservas.security.JwtAuthenticationFilter;
import com.gestion.reservas.service.UsuarioServiceImpl;
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
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = UsuarioController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioServiceImpl usuarioService;

    @Test
    void testGetAllUsuarios() throws Exception {
        UsuarioResponseDTO user = new UsuarioResponseDTO();
        user.setIdUsuario(1L);
        user.setNombre("Jorge Manchado");

        Mockito.when(usuarioService.getAllUsuariosDTO()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Jorge Manchado"));
    }


    @Test
    void testGetUsuarioById_found() throws Exception {
        Usuario mockUsuario = new Usuario();
        mockUsuario.setIdUsuario(1L);
        mockUsuario.setNombre("Ana García");

        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setIdUsuario(1L);
        dto.setNombre("Ana García");

        Mockito.when(usuarioService.findById(1L)).thenReturn(Optional.of(mockUsuario));
        Mockito.when(usuarioService.toDTO(mockUsuario)).thenReturn(dto);

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ana García"));
    }


    @Test
    void testGetUsuarioById_notFound() throws Exception {
        Mockito.when(usuarioService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/usuarios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateUsuario_success() throws Exception {
        UsuarioResponseDTO response = new UsuarioResponseDTO();
        response.setIdUsuario(1L);
        response.setNombre("Carlos López");

        Mockito.when(usuarioService.registrarUsuario(any())).thenReturn(response);

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "nombre": "Carlos López",
                                "email": "carlos@example.com",
                                "password": "1234"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Carlos López"));
    }

    @Test
    void testCreateUsuario_conflict() throws Exception {
        Mockito.when(usuarioService.registrarUsuario(any()))
                .thenThrow(new IllegalStateException("Correo ya registrado"));

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "nombre": "Carlos López",
                                "email": "carlos@example.com",
                                "password": "1234"
                            }
                        """))
                .andExpect(status().isConflict())
                .andExpect(content().string("Correo ya registrado"));
    }

    @Test
    void testInactivarUsuario_success() throws Exception {
        Mockito.when(usuarioService.inactivarUsuario(1L)).thenReturn(true);

        mockMvc.perform(put("/api/usuarios/inactivar/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Usuario inactivado correctamente"));
    }

    @Test
    void testInactivarUsuario_notFound() throws Exception {
        Mockito.when(usuarioService.inactivarUsuario(99L)).thenReturn(false);

        mockMvc.perform(put("/api/usuarios/inactivar/99"))
                .andExpect(status().isNotFound());
    }
}
