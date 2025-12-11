package com.one.flightontime.infra.ds.dto;

import lombok.Data;

@Data
public class PredictionRequest {
    //AQUI VAMOS TER QUE AGUARDAR A EQUIPE DE DS PARA COLOCARMOS QUAIS VAO SER AS CHAMADAS, MAS POR ENQUANTO VOU DEIXAR ESSES 2 DE EXEMPLO
    private Long id;
    private String text;
    private Double number;
}
