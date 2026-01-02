package com.one.flightontime.controllers;

import com.one.flightontime.infra.ds.dto.PredictionRequest;
import com.one.flightontime.infra.ds.dto.PredictionResponse;
import com.one.flightontime.service.HistoricoService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/predict")
@Validated
@Slf4j
public class RequestController {

    private final HistoricoService historicoService;

    @PostMapping
    public ResponseEntity<@NonNull PredictionResponse> receberDadosApi(@RequestBody @Valid PredictionRequest dados){
        PredictionResponse enviarDados = historicoService.prediction(dados);
        log.debug("Dados recebidos na API: companhia {}, origem {}, destino {}, data-hora {}",
                dados.codCompanhia(), dados.codAeroportoOrigem(), dados.codAeroportoDestino(), dados.dataHoraPartida()
        );
        return ResponseEntity.ok(enviarDados);
    }
}
