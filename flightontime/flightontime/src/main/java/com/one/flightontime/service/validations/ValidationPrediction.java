package com.one.flightontime.service.validations;

import com.one.flightontime.infra.ds.dto.PredictionRequest;
import com.one.flightontime.infra.exceptions.DataHoraPartidaInvalidaException;
import com.one.flightontime.infra.exceptions.OrigemDestinoException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Component
public class ValidationPrediction {

    public void validateOrigemDestino(PredictionRequest aeroporto1){
        if(aeroporto1.codAeroportoOrigem().equals(aeroporto1.codAeroportoDestino())){
           throw new OrigemDestinoException("O aeroporto de origem não pode ser igual ao de destino");
        }

        if(aeroporto1.dataHoraPartida().isBefore(OffsetDateTime.now().minusMinutes(1))){
            throw new DataHoraPartidaInvalidaException("A data e hora de partida não pode estar no passado");
        }

        if(aeroporto1.dataHoraPartida().isAfter(OffsetDateTime.now().plusDays(365))){
            throw new DataHoraPartidaInvalidaException(
                    "A data e hora de partida não pode ser maior que um ano a partir da data atual"
            );
        }
    }

}
