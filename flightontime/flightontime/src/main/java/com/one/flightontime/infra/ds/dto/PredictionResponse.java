package com.one.flightontime.infra.ds.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PredictionResponse (
    @NotBlank(message = "Campo Obrigatório")
    @JsonProperty("status_predicao")
    String status_predicao,

    @NotNull(message = "Probabilidade obrigatória")
    @JsonProperty("probabilidade")
    Double probabilidade,

    @JsonProperty("mensagem")
    String mensagem
) {}
