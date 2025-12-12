package com.one.flightontime.domain;

import com.one.flightontime.domain.enums.StatusPredicao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "HistoricoPrevisao")
public class HistoricoPrevisao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHistorico;

    private String codCompanhia;

    private String codAeroportoOrigem;

    private String codAeroportoDestino;

    private LocalDateTime dataHoraPartida;

    private Double distanciaKm;

    @Enumerated(EnumType.STRING)
    private StatusPredicao statusPredicao;

    private Double probabilidade;

    private LocalDateTime dataConsulta;

    @PrePersist
    private void preencherData(){
        this.dataConsulta = LocalDateTime.now();
    }

}
