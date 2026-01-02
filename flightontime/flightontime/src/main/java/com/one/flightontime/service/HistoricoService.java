package com.one.flightontime.service;

import com.one.flightontime.domain.HistoricoPrevisao;
import com.one.flightontime.domain.enums.StatusPredicao;
import com.one.flightontime.infra.ds.client.DsClient;
import com.one.flightontime.infra.ds.dto.PredictionRequest;
import com.one.flightontime.infra.ds.dto.PredictionResponse;
import com.one.flightontime.repository.HistoricoRepository;
import com.one.flightontime.service.validations.ValidationPrediction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HistoricoService {

    private final DsClient dsClient;
    private final HistoricoRepository repository;
    private final ValidationPrediction validation;

    public PredictionResponse prediction(PredictionRequest request) {
        log.debug("Predição recebida para companhia {} de {} para {} em {}", request.codCompanhia(),
                request.codAeroportoOrigem(), request.codAeroportoDestino(), request.dataHoraPartida());
        validation.validation(request);
        log.info("Request de predição validado com sucesso");
        PredictionResponse response;

        response = dsClient.predict(request);
        log.debug("Response carregado com sucesso do DS: status - {}, probabilidade - {}",
                response.status_predicao(), response.probabilidade());

        StatusPredicao status = StatusPredicao.valueOf(response.status_predicao().toUpperCase());

        HistoricoPrevisao historico = new HistoricoPrevisao();
        historico.setCodCompanhia(request.codCompanhia());
        historico.setCodAeroportoOrigem(request.codAeroportoOrigem());
        historico.setCodAeroportoDestino(request.codAeroportoDestino());
        historico.setDataHoraPartida(request.dataHoraPartida());
        historico.setStatusPredicao(status);
        historico.setProbabilidade(response.probabilidade());

        repository.save(historico);
        log.info("Histórico de predição salvo com sucesso: {}", historico.getIdHistorico());

        return PredictionResponse.builder()
                .status_predicao(status.name())
                .probabilidade(formatarProbabilidade(response.probabilidade()))
                .messagem("Predição realizada com sucesso")
                .build();
    }

    private Double formatarProbabilidade(Double probabilidade) {
        return Math.round(probabilidade * 100.0) / 100.0;
    }

}