package com.gestion.reservas.controller;

import com.gestion.reservas.dto.LoginRequestDTO;
import com.gestion.reservas.dto.LoginResponseDTO;
import com.gestion.reservas.entity.Usuario;
import com.gestion.reservas.repository.UsuarioRepository;
import com.gestion.reservas.security.JwtService;
import com.gestion.reservas.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.AuthProvider;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

   private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginDTO) {

        LoginResponseDTO response = authService.login(loginDTO);
        return ResponseEntity.ok(response);
    }

}