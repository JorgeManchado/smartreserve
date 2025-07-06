package com.gestion.reservas.controller;

import com.gestion.reservas.entity.Rol;
import com.gestion.reservas.security.JwtAuthenticationFilter;
import com.gestion.reservas.service.RolService;
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
        controllers = RolController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)

class RolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RolService rolService;

    @Test
    void testGetAllRoles() throws Exception {
        List<Rol> roles = List.of(
                new Rol(1L, "Administrador"),
                new Rol(2L, "Empleado")
        );

        Mockito.when(rolService.findAll()).thenReturn(roles);

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].descripcion").value("Administrador"))
                .andExpect(jsonPath("$[1].descripcion").value("Empleado"));
    }

    @Test
    void testGetRolById_found() throws Exception {
        Rol rol = new Rol(1L, "Editor");

        Mockito.when(rolService.findById(1L)).thenReturn(Optional.of(rol));

        mockMvc.perform(get("/api/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idRol").value(1))
                .andExpect(jsonPath("$.descripcion").value("Editor"));
    }

    @Test
    void testGetRolById_notFound() throws Exception {
        Mockito.when(rolService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/roles/999"))
                .andExpect(status().isNotFound());
    }
}
