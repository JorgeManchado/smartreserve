package com.gestion.reservas.service;

import com.gestion.reservas.dto.LoginRequestDTO;
import com.gestion.reservas.dto.LoginResponseDTO;
import com.gestion.reservas.entity.Usuario;
import com.gestion.reservas.exception.CredencialesInvalidasException;
import com.gestion.reservas.exception.UsuarioInactivoException;
import com.gestion.reservas.repository.UsuarioRepository;
import com.gestion.reservas.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getEmail(),
                            loginDTO.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            System.err.println("Error de autenticación: " + e.getMessage());
            throw new CredencialesInvalidasException("Correo o contraseña incorrectos.");
        }



        // Obtener usuario
        Usuario usuario = usuarioRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Verificar que el estado sea Activo
        if (usuario.getEstado() == null || !usuario.getEstado().getDescripcion().equalsIgnoreCase("Activo")) {
            throw new UsuarioInactivoException("Usuario inactivo, no puede iniciar sesión.");
        }


        // Actualizar último acceso
        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);

        // Generar token
        String token = jwtService.generateToken(usuario);

        return new LoginResponseDTO(token);
    }

}