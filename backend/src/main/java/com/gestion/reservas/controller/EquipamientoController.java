package com.gestion.reservas.controller;


import com.gestion.reservas.dto.EquipamientoDTO;
import com.gestion.reservas.entity.Rol;
import com.gestion.reservas.service.EquipamientoService;
import com.gestion.reservas.service.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/equipamientos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EquipamientoController {

    private final EquipamientoService equipamientoService;


    @GetMapping
    public ResponseEntity<List<EquipamientoDTO>> getAllEquipamientos() {
        return ResponseEntity.ok(equipamientoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipamientoDTO> getEquipamientoById(@PathVariable Long id) {
        return equipamientoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }




}
