package com.gestion.reservas.mapper;

import com.gestion.reservas.dto.UsuarioRequestDTO;
import com.gestion.reservas.dto.UsuarioResponseDTO;
import com.gestion.reservas.entity.EstadoUsuario;
import com.gestion.reservas.entity.Rol;
import com.gestion.reservas.entity.Usuario;
import com.gestion.reservas.repository.EstadoUsuarioRepository;
import com.gestion.reservas.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UsuarioMapper {

    private final RolRepository rolRepository;
    private final EstadoUsuarioRepository estadoUsuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuario toEntity(UsuarioRequestDTO dto, Usuario usuarioExistente) {
        Rol rol = rolRepository.findById(dto.getIdRol())
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
        EstadoUsuario estado = estadoUsuarioRepository.findById(dto.getIdEstado())
                .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado"));

        Usuario usuario = usuarioExistente != null ? usuarioExistente : new Usuario();

        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefono(dto.getTelefono());
        usuario.setDireccion(dto.getDireccion());
        usuario.setRol(rol);
        usuario.setEstado(estado);

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return usuario;
    }

    public UsuarioResponseDTO toDTO(Usuario usuario) {
        return UsuarioResponseDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .telefono(usuario.getTelefono())
                .direccion(usuario.getDireccion())
                .ultimoAcceso(usuario.getUltimoAcceso())
                .idRol(usuario.getRol().getIdRol())
                .rol(usuario.getRol().getDescripcion())
                .idEstado(usuario.getEstado().getIdEstado())
                .estado(usuario.getEstado().getDescripcion())
                .build();
    }

    public List<UsuarioResponseDTO> toDTOList(List<Usuario> usuarios) {
        return usuarios.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}