package com.gestion.reservas.controller;


import com.gestion.reservas.entity.Rol;
import com.gestion.reservas.entity.TipoEspacio;
import com.gestion.reservas.service.RolService;
import com.gestion.reservas.service.TipoEspacioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/tiposespacios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TipoEspacioController {

    private final TipoEspacioService tipoEspacioService;


    @GetMapping
    public ResponseEntity<List<TipoEspacio>> getAllTiposEspacio() {
        return ResponseEntity.ok(tipoEspacioService.findAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<TipoEspacio> getRolById(@PathVariable Long id) {
        return tipoEspacioService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
