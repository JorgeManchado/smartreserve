package com.gestion.reservas.service;


import com.gestion.reservas.dto.EquipamientoDTO;
import com.gestion.reservas.entity.Equipamiento;
import com.gestion.reservas.repository.EquipamientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EquipamientoServiceImpl implements EquipamientoService {

    private final EquipamientoRepository equipamientoRepository;

    @Override
    public List<EquipamientoDTO> findAll() {
        return equipamientoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<EquipamientoDTO> findById(Long id) {
        return equipamientoRepository.findById(id)
                .map(this::toDTO);
    }

    private EquipamientoDTO toDTO(Equipamiento equipamiento) {
        return EquipamientoDTO.builder()
                .idEquipamiento(equipamiento.getIdEquipamiento())
                .descripcion(equipamiento.getDescripcion())
                .build();
    }



}
