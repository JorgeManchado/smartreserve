package com.gestion.reservas.mapper;

import com.gestion.reservas.dto.*;
import com.gestion.reservas.entity.Espacio;
import com.gestion.reservas.entity.EstadoReserva;
import com.gestion.reservas.entity.Reserva;
import com.gestion.reservas.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MisReservasMapper {

    private final UsuarioMapper usuarioMapper;


    public MisReservasDTO toDTO(Reserva reserva) {
        TipoEspacioDTO tipoDTO = TipoEspacioDTO.builder()
                .idTipoEspacio(reserva.getEspacio().getTipoEspacio().getIdTipoEspacio())
                .descripcion(reserva.getEspacio().getTipoEspacio().getDescripcion())
                .build();

        EstadoEspacioDTO estadoEspacioDTO = EstadoEspacioDTO.builder()
                .idEstado(reserva.getEspacio().getEstado().getIdEstado())
                .descripcion(reserva.getEspacio().getEstado().getDescripcion())
                .build();


        EspacioResponseDTO espacioDTO = EspacioResponseDTO.builder()
                .idEspacio(reserva.getEspacio().getIdEspacio())
                .nombre(reserva.getEspacio().getNombre())
                .capacidad(reserva.getEspacio().getCapacidad())
                .ubicacion(reserva.getEspacio().getUbicacion())
                .descripcion(reserva.getEspacio().getDescripcion())
                .imagen(reserva.getEspacio().getImagen())
                .tipo(tipoDTO)
                .estado(estadoEspacioDTO)
                .build();



        EstadoReservaDTO estadoDTO = EstadoReservaDTO.builder()
                .idEstado(reserva.getEstado().getIdEstado())
                .descripcion(reserva.getEstado().getDescripcion())
                .color(reserva.getEstado().getColor())
                .bgcolor(reserva.getEstado().getBgcolor())
                .build();

        return MisReservasDTO.builder()
                .idReserva(reserva.getIdReserva())
                .usuario(usuarioMapper.toDTO(reserva.getUsuario()))
                .espacio(espacioDTO)
                .estado(estadoDTO)
                .fechaInicio(reserva.getFechaInicio())
                .fechaFin(reserva.getFechaFin())
                .sincronizado(reserva.getSincronizado())
                .ocupantes(reserva.getOcupantes())
                .recomendadaia(reserva.getRecomendadaia())
                .eventid(reserva.getEventid())
                .build();
    }

    public Reserva toEntity(MisReservasDTO dto, Usuario usuario, Espacio espacio, EstadoReserva estado) {
        return Reserva.builder()
                .idReserva(dto.getIdReserva())
                .usuario(usuario)
                .espacio(espacio)
                .estado(estado)
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .sincronizado(dto.isSincronizado())
                .ocupantes(dto.getOcupantes())
                .recomendadaia(dto.isRecomendadaia())
                .eventid(dto.getEventid())
                .usadaenmodelo(false)
                .build();
    }
}