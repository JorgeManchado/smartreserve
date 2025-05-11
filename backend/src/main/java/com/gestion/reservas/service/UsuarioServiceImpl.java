package com.gestion.reservas.service;


import com.gestion.reservas.dto.PerfilInfoDTO;
import com.gestion.reservas.dto.UsuarioRequestDTO;
import com.gestion.reservas.dto.UsuarioResponseDTO;
import com.gestion.reservas.entity.Usuario;
import com.gestion.reservas.mapper.UsuarioMapper;
import com.gestion.reservas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificacionService notificacionService;
    private final UsuarioMapper usuarioMapper;

    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    public UsuarioResponseDTO registrarUsuario(UsuarioRequestDTO dto) {
        Optional<Usuario> existente = findByEmail(dto.getEmail());
        if (existente.isPresent()) {
            throw new IllegalStateException("El correo electrónico ya está registrado.");
        }

        Usuario nuevo = toEntity(dto, null);
        Usuario guardado = save(nuevo);

        // Crear notificación
        String mensaje = String.format(
                "El usuario se ha creado el día %s.",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
        notificacionService.crearYEnviarNotificacion(guardado, mensaje);
        return toDTO(guardado);
    }

    @Override
    public Optional<UsuarioResponseDTO> actualizarUsuario(Long id, UsuarioRequestDTO dto) {
        return usuarioRepository.findById(id).map(existing -> {

            // Validar si el correo ya existe en otro usuario
            Optional<Usuario> existente = findByEmail(dto.getEmail());
            if (existente.isPresent() && !existente.get().getIdUsuario().equals(id)) {
                throw new IllegalStateException("El correo electrónico ya está registrado.");
            }

            Usuario usuarioActualizado = toEntity(dto, existing);
            usuarioActualizado.setIdUsuario(id);
            Usuario guardado = usuarioRepository.save(usuarioActualizado);

            // Crear notificación
            String mensaje = String.format(
                    "Los datos del usuario se han modificado el día %s.",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            );
            notificacionService.crearYEnviarNotificacion(guardado, mensaje);

            return toDTO(guardado);
        });
    }

    public Usuario toEntity(UsuarioRequestDTO dto, Usuario usuarioExistente) {
        return usuarioMapper.toEntity(dto, usuarioExistente);
    }

    public UsuarioResponseDTO toDTO(Usuario usuario) {
        return usuarioMapper.toDTO(usuario);
    }

    public List<UsuarioResponseDTO> getAllUsuariosDTO() {
        return usuarioMapper.toDTOList(usuarioRepository.findAll());
    }


    public boolean changePassword(Long id, String currentPassword, String newPassword) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) return false;

        Usuario usuario = usuarioOpt.get();

        if (!passwordEncoder.matches(currentPassword, usuario.getPassword())) {
            return false; // Contraseña actual incorrecta
        }

        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);

        // Crear notificación
        String mensaje = String.format(
                "La contraseña se ha actualizado el día %s.",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
        notificacionService.crearYEnviarNotificacion(usuario, mensaje);
        return true;
    }


    public Optional<UsuarioResponseDTO> actualizarPerfil(Long id, PerfilInfoDTO dto) {
        return usuarioRepository.findById(id).map(existing -> {
            existing.setNombre(dto.getNombre());
            existing.setEmail(dto.getEmail());
            existing.setTelefono(dto.getTelefono());
            existing.setDireccion(dto.getDireccion());

            Usuario actualizado = usuarioRepository.save(existing);

            String mensaje = String.format(
                    "El perfil se ha actualizado el día %s.",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            );
            notificacionService.crearYEnviarNotificacion(actualizado, mensaje);

            return toDTO(actualizado);
        });
    }

}


