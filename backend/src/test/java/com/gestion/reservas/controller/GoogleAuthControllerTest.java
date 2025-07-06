package com.gestion.reservas.controller;

import com.gestion.reservas.security.GoogleTokenStore;
import com.gestion.reservas.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = GoogleAuthController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
class GoogleAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GoogleTokenStore tokenStore;

    // Este test valida que la redirección hacia Google se construya correctamente
    @Test
    void testRedirectToGoogle() throws Exception {
        mockMvc.perform(get("/api/google/auth"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("https://accounts.google.com/o/oauth2/v2/auth")));
    }

    // Este test simula que ya hay token almacenado
    @Test
    void testTokenPresente_true() throws Exception {
        when(tokenStore.hasAccessToken()).thenReturn(true);

        mockMvc.perform(get("/api/google/token-status"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testTokenPresente_false() throws Exception {
        when(tokenStore.hasAccessToken()).thenReturn(false);

        mockMvc.perform(get("/api/google/token-status"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}
