package com.one.flightontime.infra.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PredictionIntegrationException.class)
    public ResponseEntity<ApiErrorResponse> handlePredictionIntegration(
            PredictionIntegrationException ex
    ) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ApiErrorResponse(
                        ex.getMessage(),
                        HttpStatus.BAD_GATEWAY.value(),
                        OffsetDateTime.now()
                ));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse(
                        ex.getMessage(),
                        HttpStatus.BAD_REQUEST.value(),
                        OffsetDateTime.now()
                ));
    }
}
