package com.gestion.reservas.service;


import com.gestion.reservas.entity.EstadoReserva;
import com.gestion.reservas.repository.EstadoReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EstadoReservaServiceImpl implements EstadoReservaService {

    private final EstadoReservaRepository estadoReservaRepository;

    @Override
    public Optional<EstadoReserva> findById(Long id) {
        return estadoReservaRepository.findById(id);
    }

    @Override
    public List<EstadoReserva> findAll() {
        return estadoReservaRepository.findAll();
    }
}
