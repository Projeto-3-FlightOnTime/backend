package com.one.flightontime.infra.ds.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PredictionRequest(

        @NotBlank(message = "Campo Obrigatório")
        @Size(min = 3,max = 3,message = "A sigla deve conter 3 caracteres")
        @Pattern(regexp = "^[A-Za-z]{3}$")
        @JsonProperty("cod_companhia")
        String codCompanhia,

        @NotBlank(message = "Campo Obrigatório")
        @Size(min = 4,max = 4,message = "A sigla deve conter 4 caracteres")
        @Pattern(regexp = "^[A-Za-z]{4}$")
        @JsonProperty("cod_aeroporto_origem")
        String codAeroportoOrigem,

        @NotBlank(message = "Campo Obrigatório")
        @Size(min = 4,max = 4,message = "A sigla deve conter 4 caracteres")
        @Pattern(regexp = "^[A-Za-z]{4}$")
        @JsonProperty("cod_aeroporto_destino")
        String codAeroportoDestino,

        @Future(message = "A data deve ser no futuro")
        @NotNull(message = "A data é obrigatória")
        @JsonProperty("data_hora_partida")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime dataHoraPartida,

        @Positive(message = "A distancia deve ser positiva")
        @NotNull(message = "A distância é Obrigatória")
        @JsonProperty("distancia_km")
        Integer distanciaKm
) {}