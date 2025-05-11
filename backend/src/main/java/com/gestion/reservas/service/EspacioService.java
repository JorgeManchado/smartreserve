package com.gestion.reservas.service;

import com.gestion.reservas.dto.EspacioRequestDTO;
import com.gestion.reservas.dto.EspacioResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EspacioService {
    List<EspacioResponseDTO> findAll();
    EspacioResponseDTO findById(Long id);

    EspacioResponseDTO create(String nombre, int capacidad, String ubicacion,
                              String descripcion, Long idTipoEspacio, Long idEstado,
                              List<Long> equipamientos, MultipartFile imagen);

    EspacioResponseDTO update(
            Long id,
            String nombre,
            int capacidad,
            String ubicacion,
            String descripcion,
            Long idTipoEspacio,
            Long idEstado,
            List<Long> equipamientos,
            MultipartFile imagen
    );

    void delete(Long id);
}
