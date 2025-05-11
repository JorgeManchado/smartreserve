package com.gestion.reservas.service;

import com.gestion.reservas.dto.CambioPasswordDTO;
import com.gestion.reservas.dto.PerfilInfoDTO;
import com.gestion.reservas.dto.UsuarioRequestDTO;
import com.gestion.reservas.dto.UsuarioResponseDTO;
import com.gestion.reservas.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    List<Usuario> findAll();
    Optional<Usuario> findById(Long id);
    Usuario save(Usuario usuario);
    void deleteById(Long id);
    Optional<Usuario> findByEmail(String email);
    boolean changePassword(Long id, String currentPassword, String newPassword);
    public UsuarioResponseDTO toDTO(Usuario usuario);
    Optional<UsuarioResponseDTO> actualizarPerfil(Long id, PerfilInfoDTO dto);
    UsuarioResponseDTO registrarUsuario(UsuarioRequestDTO dto);
    Optional<UsuarioResponseDTO> actualizarUsuario(Long id, UsuarioRequestDTO dto);
}


