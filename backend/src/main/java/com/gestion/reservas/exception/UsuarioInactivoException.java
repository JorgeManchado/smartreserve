package com.gestion.reservas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class UsuarioInactivoException extends RuntimeException {
    public UsuarioInactivoException(String mensaje) {
        super(mensaje);
    }
}
