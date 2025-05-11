package com.gestion.reservas.controller;

import com.gestion.reservas.entity.EstadoUsuario;

import com.gestion.reservas.service.EstadoUsuarioService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/estadosusuario")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EstadoUsuarioController {

    private final EstadoUsuarioService estadoUsuarioService;

    @GetMapping
    public ResponseEntity<List<EstadoUsuario>> getAllEstadosUsuario() {
        return ResponseEntity.ok(estadoUsuarioService.findAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<EstadoUsuario> getEstadoUsuarioById(@PathVariable Long id) {
        return estadoUsuarioService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
