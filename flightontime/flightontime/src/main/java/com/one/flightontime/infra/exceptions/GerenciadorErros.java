package com.one.flightontime.infra.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GerenciadorErros {

    @ExceptionHandler(OrigemDestinoException.class)
    public ResponseEntity<String> tratarOrigemDestino(OrigemDestinoException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
