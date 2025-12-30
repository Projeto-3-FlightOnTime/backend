package com.one.flightontime.service.validations;

import com.one.flightontime.infra.ds.dto.PredictionRequest;
import com.one.flightontime.infra.exceptions.DataHoraPartidaInvalidaException;
import com.one.flightontime.infra.exceptions.OrigemDestinoException;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class ValidationPrediction {

    public void validate(PredictionRequest request) {

        validarOrigemDestino(request);
        validarDataHoraPartida(request.dataHoraPartida());
    }

    private void validarOrigemDestino(PredictionRequest request) {
        if (request.codAeroportoOrigem().equals(request.codAeroportoDestino())) {
            throw new OrigemDestinoException(
                    "O aeroporto de origem não pode ser igual ao de destino"
            );
        }
    }

    private void validarDataHoraPartida(OffsetDateTime dataHoraPartida) {

        if (dataHoraPartida == null) {
            throw new DataHoraPartidaInvalidaException(
                    "A data e hora de partida é obrigatória"
            );
        }

        OffsetDateTime agora = OffsetDateTime.now();

        if (dataHoraPartida.isBefore(agora)) {
            throw new DataHoraPartidaInvalidaException(
                    "A data e hora de partida não pode estar no passado"
            );
        }

        if (dataHoraPartida.isAfter(agora.plusYears(1))) {
            throw new DataHoraPartidaInvalidaException(
                    "A data e hora de partida não pode ser maior que um ano a partir da data atual"
            );
        }
    }
}
