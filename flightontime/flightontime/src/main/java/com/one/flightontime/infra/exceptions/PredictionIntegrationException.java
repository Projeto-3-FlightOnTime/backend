package com.one.flightontime.infra.exceptions;

public class PredictionIntegrationException extends RuntimeException {

  public PredictionIntegrationException(String message) {
    super(message);
  }

  public PredictionIntegrationException(String message, Throwable cause) {
    super(message, cause);
  }
}

