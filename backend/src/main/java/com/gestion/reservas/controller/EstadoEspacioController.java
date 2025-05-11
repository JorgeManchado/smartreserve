package com.gestion.reservas.controller;


import com.gestion.reservas.entity.EstadoEspacio;
import com.gestion.reservas.service.EstadoEspacioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/estadosespacios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EstadoEspacioController {

    private final EstadoEspacioService estadoEspacioService;


    @GetMapping
    public ResponseEntity<List<EstadoEspacio>> getAllEstadosEspacio() {
        return ResponseEntity.ok(estadoEspacioService.findAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<EstadoEspacio> getRolById(@PathVariable Long id) {
        return estadoEspacioService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
