package com.gestion.reservas.service;

import com.gestion.reservas.dto.LoginRequestDTO;
import com.gestion.reservas.dto.LoginResponseDTO;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO loginDTO);
}
