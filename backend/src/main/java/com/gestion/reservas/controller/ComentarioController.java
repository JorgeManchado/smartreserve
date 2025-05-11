package com.gestion.reservas.controller;

import com.gestion.reservas.dto.ComentarioDTO;
import com.gestion.reservas.service.ComentarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/comentarios")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ComentarioController {

    private final ComentarioService comentarioService;

    @GetMapping
    public List<ComentarioDTO> listar() {
        return comentarioService.listarComentarios();
    }

    @PutMapping("/aprobar/{id}")
    public void aprobar(@PathVariable Long id) {
        comentarioService.aprobarComentario(id);
    }

    @PutMapping("/anular/{id}")
    public void anular(@PathVariable Long id) {
        comentarioService.anularComentario(id);
    }
}
