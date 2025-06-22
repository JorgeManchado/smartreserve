package com.gestion.reservas.service;

import com.gestion.reservas.dto.EspacioComentariosDTO;

import java.util.List;

public interface EspacioComentariosService {

    List<EspacioComentariosDTO> obtenerResumenComentarios();
}
