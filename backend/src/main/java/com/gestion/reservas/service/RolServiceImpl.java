package com.gestion.reservas.service;


import com.gestion.reservas.entity.Rol;
import com.gestion.reservas.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;

    @Override
    public Optional<Rol> findById(Long id) {
        return rolRepository.findById(id);
    }

    @Override
    public List<Rol> findAll() {
        return rolRepository.findAll();
    }
}
