package com.gestion.reservas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaDisponibilidadDTO {
    private Long idEspacio;
    private String inicio;
    private String fin;

}
