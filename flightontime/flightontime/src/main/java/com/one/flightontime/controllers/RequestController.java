package com.one.flightontime.controllers;

import com.one.flightontime.infra.ds.dto.PredictionRequest;
import com.one.flightontime.infra.ds.dto.PredictionResponse;
import com.one.flightontime.service.HistoricoService;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/predict")
public class RequestController {

    private final HistoricoService historicoService;

    @PostMapping
    public ResponseEntity<PredictionResponse> receberDadosApi(@RequestBody @Valid PredictionRequest dados){
        PredictionResponse enviarDados = historicoService.prediction(dados);

        return ResponseEntity.ok(enviarDados);
    }
}
