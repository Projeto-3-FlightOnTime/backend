package com.one.flightontime.infra.ds.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PredictionRequest(

        @JsonProperty("cod_companhia")
        String codCompanhia,
        @JsonProperty("cod_aeroporto_origem")
        String codAeroportoOrigem,
        @JsonProperty("cod_aeroporto_destino")
        String codAeroportoDestino,
        @JsonProperty("data_hora_partida")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime dataHoraPartida,
        @JsonProperty("distancia_km")
        Integer distanciaKm
) {}