package com.one.flightontime.service;

import com.one.flightontime.domain.HistoricoPrevisao;
import com.one.flightontime.domain.enums.StatusPredicao;
import com.one.flightontime.infra.ds.client.DsClient;
import com.one.flightontime.infra.ds.dto.PredictionRequest;
import com.one.flightontime.infra.ds.dto.PredictionResponse;
import com.one.flightontime.repository.HistoricoRepository;
import com.one.flightontime.service.validations.ValidationPrediction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HistoricoService {

    private final DsClient dsClient;
    private final HistoricoRepository repository;
    private final ValidationPrediction validation;

    public PredictionResponse prediction(PredictionRequest request) {
        validation.validateOrigemDestino(request);
        PredictionResponse response;

        try {
            response = dsClient.predict(request);
        } catch (Exception ex) {
            throw new RuntimeException("Error"); // TODO -> TRATAR MELHOR A MENSAGEM DE EXCESSÃO
        }

        StatusPredicao status = StatusPredicao.valueOf(response.status_predicao().toUpperCase());

        HistoricoPrevisao historico = new HistoricoPrevisao();
        historico.setCodCompanhia(request.codCompanhia());
        historico.setCodAeroportoOrigem(request.codAeroportoOrigem());
        historico.setCodAeroportoDestino(request.codAeroportoDestino());
        historico.setDataHoraPartida(request.dataHoraPartida());
        historico.setDistanciaKm(request.distanciaKm().doubleValue());
        historico.setStatusPredicao(status);
        historico.setProbabilidade(response.probabilidade().doubleValue());

        repository.save(historico);

        return PredictionResponse.builder()
                .status_predicao(status.name())
                .probabilidade(response.probabilidade())
                .messagem("Predição realizada com sucesso")
                .build();
    }

}