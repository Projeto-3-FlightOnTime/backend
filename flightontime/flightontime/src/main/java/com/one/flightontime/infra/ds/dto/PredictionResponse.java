package com.one.flightontime.infra.ds.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PredictionResponse (
    //ESSES CAMPOS DEVEM SER IGUAIS AOS RETORNADOS DO DS. VAMOS TER QUE AGUARDAR A EQUIDE DE DS.

    @NotBlank(message = "Campo Obrigatório")
    @JsonProperty("status_predicao")
    String status_predicao,

    @NotNull(message = "Probabilidade obrigatória")
    @JsonProperty("probabilidade")
    Number probabilidade,

    @JsonProperty("messagem")
    String messagem
) {}
