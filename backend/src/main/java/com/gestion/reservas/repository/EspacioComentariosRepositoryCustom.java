package com.gestion.reservas.repository;

import com.gestion.reservas.dto.EspacioComentariosDTO;

import java.util.List;

public interface  EspacioComentariosRepositoryCustom {

    List<EspacioComentariosDTO> obtenerResumenComentariosPorEspacio();
}
