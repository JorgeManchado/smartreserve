package com.gestion.reservas.controller;

import com.gestion.reservas.dto.CambioPasswordDTO;
import com.gestion.reservas.dto.PerfilInfoDTO;
import com.gestion.reservas.dto.UsuarioResponseDTO;
import com.gestion.reservas.entity.Usuario;
import com.gestion.reservas.security.JwtAuthenticationFilter;
import com.gestion.reservas.service.UsuarioService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = PerfilController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
class PerfilControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @Test
    void testGetPerfilUsuario_found() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);

        UsuarioResponseDTO dto = UsuarioResponseDTO.builder()
                .idUsuario(1L)
                .nombre("María")
                .email("maria@correo.com")
                .build();

        Mockito.when(usuarioService.findById(1L)).thenReturn(Optional.of(usuario));
        Mockito.when(usuarioService.toDTO(usuario)).thenReturn(dto);

        mockMvc.perform(get("/api/perfil/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("María"))
                .andExpect(jsonPath("$.email").value("maria@correo.com"));
    }

    @Test
    void testGetPerfilUsuario_notFound() throws Exception {
        Mockito.when(usuarioService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/perfil/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdatePerfilUsuario_found() throws Exception {
        PerfilInfoDTO dto = new PerfilInfoDTO();
        dto.setNombre("Carlos");
        dto.setEmail("carlos@correo.com");

        UsuarioResponseDTO updated = UsuarioResponseDTO.builder()
                .idUsuario(1L)
                .nombre("Carlos")
                .email("carlos@correo.com")
                .build();

        Mockito.when(usuarioService.actualizarPerfil(eq(1L), any(PerfilInfoDTO.class)))
                .thenReturn(Optional.of(updated));

        mockMvc.perform(put("/api/perfil/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nombre": "Carlos",
                                    "email": "carlos@correo.com",
                                    "telefono": "123456789",
                                    "direccion": "Calle Falsa 123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Carlos"))
                .andExpect(jsonPath("$.email").value("carlos@correo.com"));
    }

    @Test
    void testUpdatePerfilUsuario_notFound() throws Exception {
        Mockito.when(usuarioService.actualizarPerfil(eq(999L), any(PerfilInfoDTO.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/perfil/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nombre": "Carlos",
                                    "email": "carlos@correo.com",
                                    "telefono": "123456789",
                                    "direccion": "Calle Falsa 123"
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCambiarPassword_success() throws Exception {
        Mockito.when(usuarioService.changePassword(1L, "oldPass", "newPass")).thenReturn(true);

        mockMvc.perform(post("/api/perfil/cambiar-password/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "currentPassword": "oldPass",
                                    "newPassword": "newPass"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void testCambiarPassword_fail() throws Exception {
        Mockito.when(usuarioService.changePassword(1L, "wrongPass", "newPass")).thenReturn(false);

        mockMvc.perform(post("/api/perfil/cambiar-password/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "currentPassword": "wrongPass",
                                    "newPassword": "newPass"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Contraseña actual incorrecta"));
    }
}
