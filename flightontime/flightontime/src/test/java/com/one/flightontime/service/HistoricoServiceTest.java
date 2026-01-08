package com.one.flightontime.service;

import com.one.flightontime.domain.HistoricoPrevisao;
import com.one.flightontime.infra.ds.client.DsClient;
import com.one.flightontime.infra.ds.dto.PredictionRequest;
import com.one.flightontime.infra.ds.dto.PredictionResponse;
import com.one.flightontime.infra.exceptions.CodigoInvalidoException;
import com.one.flightontime.repository.HistoricoRepository;
import com.one.flightontime.service.validations.ValidationPrediction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoricoServiceTest {

    @Mock
    private HistoricoRepository historicoRepository;

    @Mock
    private DsClient dsClient;

    @Mock
    private ValidationPrediction validationPrediction;

    @InjectMocks
    private HistoricoService historicoService;

    @Test // TESTE PARA RETORNAR PONTUAL QUANDO PROBABILIDADE FOR MENOR QUE 40
    void deveRetornarPontualQuandoProbabilidadeMenorQue40() {
        PredictionRequest request = PredictionRequest.builder()
                .codCompanhia("GLO")
                .codAeroportoOrigem("SBAQ")
                .codAeroportoDestino("SCIE")
                .dataHoraPartida(OffsetDateTime.now().plusHours(2))
                .build();

        PredictionResponse dsResponse = PredictionResponse.builder()
                .status_predicao("PONTUAL")
                .probabilidade(0.35)
                .build();

        doNothing().when(validationPrediction).validation(request);

        when(dsClient.predict(request)).thenReturn(dsResponse);

        PredictionResponse response = historicoService.prediction(request);

        assertEquals("PONTUAL", response.status_predicao());
        assertEquals(0.35, response.probabilidade());

        verify(historicoRepository, times(1)).save(any(HistoricoPrevisao.class));
    }

    @Test // TESTE PARA RETORNAR ATRASADO QUANDO PROBABILIDADE FOR MAIOR QUE 40
    void deveRetornarAtrasadoQuandoProbabilidadeMaiorQue40() {
        PredictionRequest request = PredictionRequest.builder()
                .codCompanhia("GLO")
                .codAeroportoOrigem("SBAQ")
                .codAeroportoDestino("SCIE")
                .dataHoraPartida(OffsetDateTime.now().plusHours(2))
                .build();

        PredictionResponse dsResponse = PredictionResponse.builder()
                .status_predicao("ATRASADO")
                .probabilidade(0.45)
                .build();

        doNothing().when(validationPrediction).validation(request);

        when(dsClient.predict(request)).thenReturn(dsResponse);

        PredictionResponse response = historicoService.prediction(request);

        assertEquals("ATRASADO", response.status_predicao());
        assertEquals(0.45, response.probabilidade());

        verify(historicoRepository, times(1)).save(any(HistoricoPrevisao.class));
    }

    @Test // TESTE PARA RETORNAR ATRASADO QUANDO PROBABILIDADE FOR IGUAL A 40
    void deveRetornarAtrasadoQuandoProbabilidadeIgual40() {
        PredictionRequest request = PredictionRequest.builder()
                .codCompanhia("GLO")
                .codAeroportoOrigem("SBAQ")
                .codAeroportoDestino("SCIE")
                .dataHoraPartida(OffsetDateTime.now().plusHours(2))
                .build();

        PredictionResponse dsResponse = PredictionResponse.builder()
                .status_predicao("ATRASADO")
                .probabilidade(0.40)
                .build();

        doNothing().when(validationPrediction).validation(request);

        when(dsClient.predict(request)).thenReturn(dsResponse);

        PredictionResponse response = historicoService.prediction(request);

        assertEquals("ATRASADO", response.status_predicao());
        assertEquals(0.40, response.probabilidade());

        verify(historicoRepository, times(1)).save(any(HistoricoPrevisao.class));
    }

    @Test // TESTE PARA ARREDONDAR A PROBABILIDADE CORRETAMENTE EM DUAS CASAS DECIMAIS
    void deveArredondarAProbabilidadeCorretamente() {
        PredictionRequest request = PredictionRequest.builder()
                .codCompanhia("GLO")
                .codAeroportoOrigem("SBAQ")
                .codAeroportoDestino("SCIE")
                .dataHoraPartida(OffsetDateTime.now().plusHours(2))
                .build();

        PredictionResponse dsResponse = PredictionResponse.builder()
                .status_predicao("ATRASADO")
                .probabilidade(0.450391934)
                .build();

        doNothing().when(validationPrediction).validation(request);

        when(dsClient.predict(request)).thenReturn(dsResponse);

        PredictionResponse response = historicoService.prediction(request);

        assertEquals("ATRASADO", response.status_predicao());
        assertEquals(0.45, response.probabilidade());

        verify(historicoRepository, times(1)).save(any(HistoricoPrevisao.class));
    }

    @Test // TESTE PARA LANÇAR EXCEÇÃO QUANDO A VALIDAÇÃO FALHAR
    void deveLancarExcecaoQuandoValidacaoFalhar() {
        PredictionRequest request = PredictionRequest.builder()
                .codCompanhia("XXX")
                .codAeroportoOrigem("SBAQ")
                .codAeroportoDestino("SCIE")
                .dataHoraPartida(OffsetDateTime.now().plusHours(2))
                .build();

        doThrow(new CodigoInvalidoException("Dados inválidos"))
                .when(validationPrediction).validation(request);

        CodigoInvalidoException exception = assertThrows(CodigoInvalidoException.class, () -> {
            historicoService.prediction(request);
        });

        assertEquals("Dados inválidos", exception.getMessage());

        verify(historicoRepository, never()).save(any(HistoricoPrevisao.class));
    }

    @Test // TESTE PARA LANÇAR EXCEÇÃO QUANDO O DS CLIENT FALHAR
    void deveLancarExcecaoQuandoDsClientFalhar() {
        PredictionRequest request = PredictionRequest.builder()
                .codCompanhia("GLO")
                .codAeroportoOrigem("SBAQ")
                .codAeroportoDestino("SCIE")
                .dataHoraPartida(OffsetDateTime.now().plusHours(2))
                .build();

        doNothing().when(validationPrediction).validation(request);

        when(dsClient.predict(request))
                .thenThrow(new RuntimeException("Erro no DS Client"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            historicoService.prediction(request);
        });

        assertEquals("Erro no DS Client", exception.getMessage());

        verify(historicoRepository, never()).save(any(HistoricoPrevisao.class));
    }
}