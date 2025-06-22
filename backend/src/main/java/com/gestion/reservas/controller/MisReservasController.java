package com.gestion.reservas.controller;

import com.gestion.reservas.dto.MisReservasDTO;
import com.gestion.reservas.dto.ReservaDTO;
import com.gestion.reservas.dto.ReservaDisponibilidadDTO;
import com.gestion.reservas.service.MisReservasService;
import com.gestion.reservas.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/misreservas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MisReservasController {

    private final MisReservasService reservaService;

    @GetMapping
    public List<MisReservasDTO> getAll() {
        return reservaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MisReservasDTO> getById(@PathVariable Long id) {
        return reservaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MisReservasDTO> create(@RequestBody MisReservasDTO reservaDTO) {
        try {
            MisReservasDTO created = reservaService.save(reservaDTO);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MisReservasDTO> update(@PathVariable Long id, @RequestBody MisReservasDTO misreservasDTO) {
        if (!reservaService.findById(id).isPresent()) {
            return ResponseEntity.<MisReservasDTO>notFound().build();
        }

        misreservasDTO.setIdReserva(id);

        try {
            MisReservasDTO updated = reservaService.save(misreservasDTO);
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

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarReserva(@PathVariable Long id) {
        reservaService.cancelarReserva(id);
        return ResponseEntity.noContent().build(); // HTTP 204
    }

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<Void> confirmarReserva(@PathVariable Long id) {
        reservaService.confirmarReserva(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/disponible")
    public ResponseEntity<Boolean> esEspacioDisponible(@RequestBody ReservaDisponibilidadDTO request) {
        boolean disponible = reservaService.estaDisponible (request.getIdEspacio(), request.getInicio(), request.getFin());
        return ResponseEntity.ok(disponible);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<MisReservasDTO>> getReservasPorUsuario(@PathVariable Long idUsuario) {
        List<MisReservasDTO> reservas = reservaService.obtenerReservasPorUsuario(idUsuario);
        return ResponseEntity.ok(reservas);
    }



}
