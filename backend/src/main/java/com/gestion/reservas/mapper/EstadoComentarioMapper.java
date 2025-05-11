package com.gestion.reservas.mapper;

import com.gestion.reservas.dto.EstadoComentarioDTO;
import com.gestion.reservas.entity.EstadoComentario;
import org.springframework.stereotype.Component;

@Component
public class EstadoComentarioMapper {

    public EstadoComentarioDTO toDto(EstadoComentario estado) {
        if (estado == null) return null;
        return new EstadoComentarioDTO(
                estado.getIdEstado(),
                estado.getDescripcion()
        );
    }

    public EstadoComentario toEntity(EstadoComentarioDTO dto) {
        if (dto == null) return null;
        return new EstadoComentario(
                dto.getIdEstado(),
                dto.getDescripcion()
        );
    }
}
