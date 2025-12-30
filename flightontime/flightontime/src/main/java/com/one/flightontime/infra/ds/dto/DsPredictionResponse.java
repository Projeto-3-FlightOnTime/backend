package com.one.flightontime.infra.ds.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record DsPredictionResponse(

        @JsonProperty("status_predicao")
        String status_predicao,

        @JsonProperty("probabilidade")
        Double probabilidade,

        @JsonProperty("messagem")
        String messagem
) {}
