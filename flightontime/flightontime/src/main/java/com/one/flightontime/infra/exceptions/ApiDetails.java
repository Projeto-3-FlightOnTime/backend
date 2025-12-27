package com.one.flightontime.infra.exceptions;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class ApiDetails {

    private String title;
    private String message;
    private int status;
    private LocalDateTime timestamp;
}
