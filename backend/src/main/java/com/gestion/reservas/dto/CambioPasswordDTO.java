package com.gestion.reservas.dto;



import lombok.Data;

@Data
public class CambioPasswordDTO {
    private String currentPassword;
    private String newPassword;
}
