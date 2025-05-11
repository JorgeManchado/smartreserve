package com.gestion.reservas.controller;

import com.gestion.reservas.dto.ReservaDTO;
import com.gestion.reservas.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReservaController {

    private final ReservaService reservaService;

    @GetMapping
    public List<ReservaDTO> getAll() {
        return reservaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaDTO> getById(@PathVariable Long id) {
        return reservaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ReservaDTO> create(@RequestBody ReservaDTO reservaDTO) {
        try {
            ReservaDTO created = reservaService.save(reservaDTO);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaDTO> update(@PathVariable Long id, @RequestBody ReservaDTO reservaDTO) {
        if (!reservaService.findById(id).isPresent()) {
            return ResponseEntity.<ReservaDTO>notFound().build();
        }

        reservaDTO.setIdReserva(id);

        try {
            ReservaDTO updated = reservaService.save(reservaDTO);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!reservaService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        reservaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

