package krupkoillia.chesstracker.gameservice.exception.handler;

import java.time.Instant;

public record ErrorResponse(
        int status,
        String message,
        Instant timestamp
) {}
