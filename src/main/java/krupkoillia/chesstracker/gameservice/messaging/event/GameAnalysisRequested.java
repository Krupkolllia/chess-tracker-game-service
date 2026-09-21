package krupkoillia.chesstracker.gameservice.messaging.event;

import java.time.Instant;
import java.util.UUID;

public record GameAnalysisRequested(
        UUID eventId,
        Long gameId,
        Long userId,
        String pgn,
        Instant occurredAt
) {}
