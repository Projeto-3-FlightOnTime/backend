package com.one.flightontime.infra.ds.client;

import com.one.flightontime.infra.ds.dto.PredictionRequest;
import com.one.flightontime.infra.ds.dto.DsPredictionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "dsClient", url = "http://localhost:8000")
public interface DsClient {

    @PostMapping(value = "/predict")
    DsPredictionResponse predict(@RequestBody PredictionRequest request);

}