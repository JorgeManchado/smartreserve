package com.gestion.reservas.controller;


import com.gestion.reservas.dto.EspacioComentariosDTO;
import com.gestion.reservas.service.EspacioComentariosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/espacios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EspacioComentariosController {

    private final EspacioComentariosService espacioComentariosService;

    @GetMapping("/comentarios")
    public ResponseEntity<List<EspacioComentariosDTO>> obtenerEspaciosConComentarios() {
        return ResponseEntity.ok(espacioComentariosService.obtenerResumenComentarios());
    }
}

