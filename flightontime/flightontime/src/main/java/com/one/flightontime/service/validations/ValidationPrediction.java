package com.one.flightontime.service.validations;

import com.one.flightontime.infra.ds.dto.PredictionRequest;
import com.one.flightontime.infra.exceptions.OrigemDestinoException;
import org.springframework.stereotype.Component;

@Component
public class ValidationPrediction {

    public void validateOrigemDestino(PredictionRequest aeroporto1){
        if(aeroporto1.codAeroportoOrigem().equals(aeroporto1.codAeroportoDestino())){
           throw new OrigemDestinoException("O aeroporto de origem não pode ser igual ao de destino");
        }
    }

}
