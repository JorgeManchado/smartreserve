package com.gestion.reservas.service;

import com.gestion.reservas.entity.EstadoUsuario;
import com.gestion.reservas.repository.EstadoUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EstadoUsuarioServiceImp implements EstadoUsuarioService {
    private final EstadoUsuarioRepository estadoUsuarioRepository;

    @Override
    public Optional<EstadoUsuario> findById(Long id) {
        return estadoUsuarioRepository.findById(id);
    }

    @Override
    public List<EstadoUsuario> findAll() {
        return estadoUsuarioRepository.findAll();
    }
}
