package com.gestion.reservas.mapper;

import com.gestion.reservas.dto.ComentarioDTO;
import com.gestion.reservas.entity.Comentario;
import org.springframework.stereotype.Component;

@Component
public class ComentarioMapper {

    public ComentarioDTO toDto(Comentario comentario) {
        if (comentario == null) return null;

        return new ComentarioDTO(
                comentario.getIdComentario(),
                comentario.getTexto(),
                comentario.getValoracion(),
                comentario.getFecha(),

                comentario.getEstado() != null ? comentario.getEstado().getDescripcion() : null,
                comentario.getEstado() != null ? comentario.getEstado().getIdEstado() : null,

                comentario.getReserva().getUsuario().getNombre(),
                comentario.getReserva().getUsuario().getIdUsuario(),

                comentario.getReserva().getIdReserva()
        );
    }
}
