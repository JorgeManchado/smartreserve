package com.gestion.reservas.service;



import com.gestion.reservas.entity.TipoEspacio;
import com.gestion.reservas.repository.TipoEspacioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TipoEspacioServiceImpl implements TipoEspacioService {

    private final TipoEspacioRepository tipoEspacioRepository;

    @Override
    public Optional<TipoEspacio> findById(Long id) {
        return tipoEspacioRepository.findById(id);
    }

    @Override
    public List<TipoEspacio> findAll() {
        return tipoEspacioRepository.findAll();
    }
}
