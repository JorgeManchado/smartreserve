package com.gestion.reservas.controller;

import com.gestion.reservas.dto.RecomendacionRequestDTO;

import com.gestion.reservas.dto.RecomendacionResponseDTO;
import com.gestion.reservas.service.RecomendadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recomendador")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class RecomendadorController {

    private final RecomendadorService recomendadorService;

    @PostMapping
    public ResponseEntity<RecomendacionResponseDTO> recomendar(@RequestBody RecomendacionRequestDTO request) {
        try {

             RecomendacionResponseDTO response = recomendadorService.obtenerRecomendaciones(
                    request.getUsuarioId(),
                    request.getDiaSemana(),
                    request.getHora()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/entrenar")
    public ResponseEntity<?> entrenarModelo() {
        try {

            String resultado = recomendadorService.entrenarModelo();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}
