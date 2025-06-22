package com.gestion.reservas.service;

import com.gestion.reservas.dto.ComentarioDTO;
import java.util.List;


public interface ComentarioService {

    List<ComentarioDTO> listarComentarios();
    void aprobarComentario(Long id);
    void anularComentario(Long id);
    ComentarioDTO crearComentario(ComentarioDTO dto);
}
