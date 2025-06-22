package com.gestion.reservas.service;


import com.gestion.reservas.dto.EspacioComentariosDTO;
import com.gestion.reservas.repository.EspacioComentariosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EspacioComentariosServiceImpl implements EspacioComentariosService  {

    private final EspacioComentariosRepository espacioComentariosRepository;

    public List<EspacioComentariosDTO> obtenerResumenComentarios() {
        return espacioComentariosRepository.obtenerResumenComentariosPorEspacio();
    }
}
