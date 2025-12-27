package com.one.flightontime.service.validations;

import com.one.flightontime.infra.ds.dto.PredictionRequest;
import com.one.flightontime.infra.exceptions.CodigoInvalidoException;
import com.one.flightontime.infra.exceptions.DataHoraPartidaInvalidaException;
import com.one.flightontime.infra.exceptions.OrigemDestinoException;
import com.one.flightontime.service.CatalogoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class ValidationPrediction {

    private final CatalogoService service;

    public void validation(PredictionRequest aeroporto1){
        if(!service.companhiaExiste(aeroporto1.codCompanhia())){
            throw new CodigoInvalidoException("O código da companhia aérea é inválido");
        }

        if(!service.aeroportoExiste(aeroporto1.codAeroportoOrigem())){
            throw new CodigoInvalidoException("O código do aeroporto de origem é inválido");
        }

        if(!service.aeroportoExiste(aeroporto1.codAeroportoDestino())){
            throw new CodigoInvalidoException("O código do aeroporto de destino é inválido");
        }

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
