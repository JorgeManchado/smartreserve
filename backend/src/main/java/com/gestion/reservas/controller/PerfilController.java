package com.gestion.reservas.controller;


import com.gestion.reservas.dto.CambioPasswordDTO;
import com.gestion.reservas.dto.PerfilInfoDTO;
import com.gestion.reservas.dto.UsuarioResponseDTO;
import com.gestion.reservas.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/api/perfil")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PerfilController {

    private final UsuarioService usuarioService;



    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> getPerfilUsuario(@PathVariable Long id) {
        return usuarioService.findById(id)
                .map(usuarioService::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> updatePerfilUsuario(@PathVariable Long id,
                                                                  @Valid @RequestBody PerfilInfoDTO dto) {
        return usuarioService.actualizarPerfil(id, dto)
                .map(actualizadoDto -> ResponseEntity.ok(actualizadoDto))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/cambiar-password/{id}")
    public ResponseEntity<?> cambiarPassword(
            @PathVariable Long id,
            @RequestBody CambioPasswordDTO dto) {

        boolean cambiado = usuarioService.changePassword(id, dto.getCurrentPassword(), dto.getNewPassword());

        if (cambiado) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Contraseña actual incorrecta");
        }
    }

}
