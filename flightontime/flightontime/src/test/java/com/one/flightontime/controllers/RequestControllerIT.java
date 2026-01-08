package com.one.flightontime.controllers;

import com.one.flightontime.domain.HistoricoPrevisao;
import com.one.flightontime.infra.ds.client.DsClient;
import com.one.flightontime.infra.ds.dto.PredictionRequest;
import com.one.flightontime.infra.ds.dto.PredictionResponse;
import com.one.flightontime.repository.HistoricoRepository;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.util.Map;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RequestControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DsClient dsClient;

    @MockitoBean
    private HistoricoRepository repository;

    @Test // TESTE PARA REALIZAR PREDIÇÃO COM SUCESSO
    void deveRealizarPredicaoComSucesso() throws Exception {
        PredictionRequest request = request();

        PredictionResponse response = PredictionResponse.builder()
                .status_predicao("PONTUAL")
                .probabilidade(0.25)
                .build();

        when(dsClient.predict(any(PredictionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/predict")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_predicao").value("PONTUAL"))
                .andExpect(jsonPath("$.probabilidade").value(0.25))
                .andExpect(jsonPath("$.mensagem").value("Predição realizada com sucesso"));
    }

    @Test // TESTE PARA RETORNAR ERRO QUANDO A ORIGEM FOR IGUAL AO DESTINO
    void deveRetornarErroOrigemIgualDestino() throws Exception {
        PredictionRequest request = PredictionRequest.builder()
                .codCompanhia("AZU")
                .codAeroportoOrigem("SBGR")
                .codAeroportoDestino("SBGR")
                .dataHoraPartida(dataHoraPartidaFutura())
                .build();

        mockMvc.perform(post("/predict")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("O aeroporto de origem não pode ser igual ao de destino"));
    }

    @Test // TESTE PARA PERSISTIR OBJETO NO BANCO APÓS PREDIÇÃO
    void devePersistirObjetoNoBancoAposPredicao() throws Exception {
        PredictionRequest request = request();

        PredictionResponse response = PredictionResponse.builder()
                .status_predicao("ATRASADO")
                .probabilidade(0.55)
                .build();

        when(dsClient.predict(any(PredictionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/predict")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_predicao").value("ATRASADO"))
                .andExpect(jsonPath("$.probabilidade").value(0.55))
                .andExpect(jsonPath("$.mensagem").value("Predição realizada com sucesso"));

        verify(repository, times(1)).save(any(HistoricoPrevisao.class));
    }

    @Test // TESTE PARA RETORNAR ERRO QUANDO O CLIENT FALHAR
    void deveRetornarErroQuandoDSFalhar() throws Exception {
        PredictionRequest request = request();
        FeignException ex = FeignException.errorStatus(
                "predict",
                feign.Response.builder()
                        .status(500)
                        .reason("Internal Server Error")
                        .request(Request.create(Request.HttpMethod.POST, "/predict", Map.of(),
                                null, null, null))
                        .build()
        );

        when(dsClient.predict(any(PredictionRequest.class))).thenThrow(ex);

        mockMvc.perform(post("/predict")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message")
                .value("Ocorreu um erro interno no serviço de predição. Por favor, tente novamente mais tarde."));
    }

    @Test // TESTE PARA RETORNAR ERRO QUANDO CAMPO OBRIGATÓRIO FALTAR
    void deveRetornarErroQuandoCampoObrigatorioFaltar() throws Exception {
        OffsetDateTime data = dataHoraPartidaFutura();
        PredictionRequest request = PredictionRequest.builder()
                .codCompanhia(null)
                .codAeroportoOrigem("KMIA")
                .codAeroportoDestino("SBGR")
                .dataHoraPartida(data)
                .build();

        mockMvc.perform(post("/predict")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(dsClient, never()).predict(any());
    }

    @Test // TESTE PARA RETORNAR ERRO QUANDO DATA/HORA DE PARTIDA ESTIVER NO PASSADO
    void deveRetornarErroQuandoDataHoraPartidaNoPassado() throws Exception {
        OffsetDateTime data = OffsetDateTime.parse("2025-12-25T10:00:00Z");
        PredictionRequest request = PredictionRequest.builder()
                .codCompanhia("AZU")
                .codAeroportoOrigem("KMIA")
                .codAeroportoDestino("SBGR")
                .dataHoraPartida(data)
                .build();

        mockMvc.perform(post("/predict")
                .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("dataHoraPartida: A data deve ser no futuro"));

        verify(dsClient, never()).predict(any());
    }

    // TODO Código de companhia inválido

    // TODO aeroporto inexistente

    // TODO verificar se os dados foram salvos corretamente no banco

    private PredictionRequest request(){
        OffsetDateTime data = dataHoraPartidaFutura();
        return PredictionRequest.builder()
                .codCompanhia("AZU")
                .codAeroportoOrigem("KMIA")
                .codAeroportoDestino("SBGR")
                .dataHoraPartida(data)
                .build();
    }

    private OffsetDateTime dataHoraPartidaFutura(){
        return OffsetDateTime.parse("2026-01-30T10:00:00Z");
    }
}