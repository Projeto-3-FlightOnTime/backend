package com.one.flightontime.service;

import com.one.flightontime.domain.HistoricoPrevisao;
import com.one.flightontime.domain.enums.StatusPredicao;
import com.one.flightontime.infra.ds.client.DsClient;
import com.one.flightontime.infra.ds.dto.DsPredictionResponse;
import com.one.flightontime.infra.ds.dto.PredictionRequest;
import com.one.flightontime.controllers.dtoFront.PredictionResponse;
import com.one.flightontime.infra.exceptions.PredictionIntegrationException;
import com.one.flightontime.repository.HistoricoRepository;
import com.one.flightontime.service.validations.ValidationPrediction;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class HistoricoService {

    private final DsClient dsClient;
    private final HistoricoRepository repository;
    private final ValidationPrediction validation;

    public PredictionResponse prediction(PredictionRequest request) {

        // 1️⃣ Validação de regras de negócio
        validation.validate(request);

        // 2️⃣ Chamada ao serviço DS
        DsPredictionResponse dsResponse;
        try {
            dsResponse = dsClient.predict(request);
        } catch (FeignException ex) {

            log.error("Erro ao chamar DS | status={} | body={}",
                    ex.status(), ex.contentUTF8());

            if (ex.status() == 400) {
                throw new PredictionIntegrationException(
                        "Dados inválidos enviados para o serviço de predição", ex
                );
            }

            if (ex.status() == 422) {
                throw new PredictionIntegrationException(
                        "Erro de validação no serviço de predição", ex
                );
            }

            throw new PredictionIntegrationException(
                    "Serviço de predição indisponível no momento", ex
            );
        }

        // 3️⃣ Conversão segura do status
        StatusPredicao status;
        try {
            status = StatusPredicao.valueOf(
                    dsResponse.status_predicao().toUpperCase()
            );
        } catch (IllegalArgumentException ex) {
            throw new PredictionIntegrationException(
                    "Status inválido retornado pelo serviço de predição"
            );
        }

        // 4️⃣ Persistência
        HistoricoPrevisao historico = new HistoricoPrevisao();
        historico.setCodCompanhia(request.codCompanhia());
        historico.setCodAeroportoOrigem(request.codAeroportoOrigem());
        historico.setCodAeroportoDestino(request.codAeroportoDestino());
        historico.setDataHoraPartida(request.dataHoraPartida());
        historico.setStatusPredicao(status);
        historico.setProbabilidade(dsResponse.probabilidade());

        repository.save(historico);

        // 5️⃣ Resposta FINAL para o FRONT
        return PredictionResponse.builder()
                .statusPredicao(status.name())
                .probabilidade(
                        Double.parseDouble(
                                String.format(
                                        Locale.US,
                                        "%.2f",
                                        historico.getProbabilidade() * 100
                                )
                        )
                )
                .mensagem("Predição realizada com sucesso")
                .build();
    }
}
