package com.one.flightontime.controllers;

import com.one.flightontime.infra.ds.dto.PredictionRequest;
import com.one.flightontime.controllers.dtoFront.PredictionResponse;
import com.one.flightontime.service.HistoricoService;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/predict")
@Slf4j
public class RequestController {

    private final HistoricoService historicoService;

    @PostMapping
    public ResponseEntity<PredictionResponse> receberDadosApi(
            @RequestBody @Valid PredictionRequest request) {

        log.info("📥 Requisição recebida no endpoint /predict");
        log.debug("📄 Payload recebido: {}", request);

        PredictionResponse response = historicoService.prediction(request);

        log.info("📤 Resposta enviada com sucesso");

        return ResponseEntity.ok(response);
    }
}
