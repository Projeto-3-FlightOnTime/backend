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
        Double probabilidade = response.probabilidade();
        log.debug("Response carregado com sucesso do DS: status - {}, probabilidade - {}",
                response.status_predicao(), probabilidade);
        probabilidade = arredondarProbabilidade(probabilidade);
        StatusPredicao status = pontualOrAtrasado(probabilidade);

        HistoricoPrevisao historico = criarHistorico(request, status, probabilidade);
        repository.save(historico);
        log.info("Histórico de predição salvo com sucesso: {}", historico.getIdHistorico());

        return PredictionResponse.builder()
                .status_predicao(status.name())
                .probabilidade(probabilidade)
                .mensagem("Predição realizada com sucesso")
                .build();
    }

    private Double arredondarProbabilidade(Double probabilidade) {
        return Math.round(probabilidade * 100.0) / 100.0;
    }

    private StatusPredicao pontualOrAtrasado(Double probabilidade){
        return probabilidade >= 0.40 ? StatusPredicao.ATRASADO : StatusPredicao.PONTUAL;
    }

    private HistoricoPrevisao criarHistorico(PredictionRequest request, StatusPredicao status, Double probabilidade) {
        HistoricoPrevisao historico = new HistoricoPrevisao();
        historico.setCodCompanhia(request.codCompanhia());
        historico.setCodAeroportoOrigem(request.codAeroportoOrigem());
        historico.setCodAeroportoDestino(request.codAeroportoDestino());
        historico.setDataHoraPartida(request.dataHoraPartida());
        historico.setStatusPredicao(status);
        historico.setProbabilidade(probabilidade);
        return historico;
    }
}
