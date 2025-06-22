package com.gestion.reservas.controller;

import com.gestion.reservas.dto.EspacioRequestDTO;
import com.gestion.reservas.dto.EspacioResponseDTO;
import com.gestion.reservas.service.EspacioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Map;

@RestController
@RequestMapping("/api/espacios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EspacioController {

    private final EspacioService espacioService;


    @GetMapping
    public ResponseEntity<List<EspacioResponseDTO>> getAll() {
        List<EspacioResponseDTO> espacios = espacioService.findAll();
        return ResponseEntity.ok(espacios);
    }


    @GetMapping("/{id}")
    public ResponseEntity<EspacioResponseDTO> getById(@PathVariable Long id) {
        try {
            EspacioResponseDTO dto = espacioService.findById(id);
            return ResponseEntity.ok(dto);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EspacioResponseDTO> Create(
            @RequestParam String nombre,
            @RequestParam int capacidad,
            @RequestParam String ubicacion,
            @RequestParam String descripcion,
            @RequestParam Long idTipoEspacio,
            @RequestParam Long idEstado,
            @RequestParam List<Long> equipamientos,
            @RequestParam(required = false) MultipartFile imagen
    ) {

                EspacioResponseDTO dto = espacioService.create(
                nombre, capacidad, ubicacion, descripcion,
                idTipoEspacio, idEstado, equipamientos, imagen
        );
        return ResponseEntity.ok(dto);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EspacioResponseDTO> updateEspacio(
            @PathVariable Long id,
            @RequestParam String nombre,
            @RequestParam int capacidad,
            @RequestParam String ubicacion,
            @RequestParam String descripcion,
            @RequestParam Long idTipoEspacio,
            @RequestParam Long idEstado,
            @RequestParam List<Long> equipamientos,
            @RequestParam(required = false) MultipartFile imagen
    ) {
        EspacioResponseDTO dto = espacioService.update(
                id,
                nombre,
                capacidad,
                ubicacion,
                descripcion,
                idTipoEspacio,
                idEstado,
                equipamientos,
                imagen
        );
        return ResponseEntity.ok(dto);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        espacioService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{id}/actualizar-estado")
    public ResponseEntity<Void> actualizarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, Long> body
    ) {

        Long idEstado = body.get("idEstado");
        espacioService.updateEstado(id, idEstado);
        return ResponseEntity.noContent().build();
    }
}

