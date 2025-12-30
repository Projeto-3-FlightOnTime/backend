package com.one.flightontime.controllers.dtoFront;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record PredictionResponse(

        @JsonProperty("status_predicao")
        String statusPredicao,

        @JsonProperty("probabilidade")
        Double probabilidade,

        @JsonProperty("mensagem")
        String mensagem
) {}
