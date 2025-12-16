package com.one.flightontime.infra.ds.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record PredictionResponse (
    //ESSES CAMPOS DEVEM SER IGUAIS AOS RETORNADOS DO DS. VAMOS TER QUE AGUARDAR A EQUIDE DE DS.

    @JsonProperty("status_predicao")
    String status_predicao,
    @JsonProperty("probabilidade")
    Number probabilidade,
    @JsonProperty("messagem")
    String messagem
) {}
