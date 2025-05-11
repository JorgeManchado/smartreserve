package com.gestion.reservas.controller;

import com.gestion.reservas.entity.EstadoReserva;
import com.gestion.reservas.service.EstadoReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estadosreservas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EstadoReservaController {

    private final EstadoReservaService estadoReservaService;


    @GetMapping
    public ResponseEntity<List<EstadoReserva>> getAllEstadosReserva() {
        return ResponseEntity.ok(estadoReservaService.findAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<EstadoReserva> getRolById(@PathVariable Long id) {
        return estadoReservaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
