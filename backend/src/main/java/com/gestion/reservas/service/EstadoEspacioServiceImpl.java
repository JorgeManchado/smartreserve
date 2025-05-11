package com.gestion.reservas.service;



import com.gestion.reservas.entity.EstadoEspacio;
import com.gestion.reservas.entity.TipoEspacio;
import com.gestion.reservas.repository.EstadoEspacioRepository;
import com.gestion.reservas.repository.TipoEspacioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EstadoEspacioServiceImpl implements EstadoEspacioService {

    private final EstadoEspacioRepository estadoEspacioRepository;

    @Override
    public Optional<EstadoEspacio> findById(Long id) {
        return estadoEspacioRepository.findById(id);
    }

    @Override
    public List<EstadoEspacio> findAll() {
        return estadoEspacioRepository.findAll();
    }
}
