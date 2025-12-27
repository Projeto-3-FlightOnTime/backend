package com.one.flightontime.infra.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GerenciadorErros {

    @ExceptionHandler(OrigemDestinoException.class)
    public ResponseEntity<ApiDetails> tratarOrigemDestino(OrigemDestinoException ex){
        ApiDetails apiDetails = ApiDetails.builder()
                .title("O destino deve ser diferente da origem")
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiDetails);
    }

    @ExceptionHandler(DataHoraPartidaInvalidaException.class)
    public ResponseEntity<ApiDetails> tratarDataHoraPartidaInvalida(DataHoraPartidaInvalidaException ex){
        ApiDetails apiDetails = ApiDetails.builder()
                .title("Data e hora de partida inválida")
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiDetails);
    }

    @ExceptionHandler(CodigoInvalidoException.class)
    public ResponseEntity<ApiDetails> tratarCodigoInvalido(CodigoInvalidoException ex){
        ApiDetails apiDetails = ApiDetails.builder()
                .title("Código inválido")
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiDetails);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiDetails> tratarValidacaoBean(MethodArgumentNotValidException ex) {

        String mensagem = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .findFirst()
                .orElse("Erro de validação");

        ApiDetails apiDetails = ApiDetails.builder()
                .title("Erro de validação")
                .message(mensagem)
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiDetails);
    }

}
