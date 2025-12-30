package com.one.flightontime.infra.exceptions;

import java.time.OffsetDateTime;

public record ApiErrorResponse(
        String message,
        int status,
        OffsetDateTime timestamp
) {
}
