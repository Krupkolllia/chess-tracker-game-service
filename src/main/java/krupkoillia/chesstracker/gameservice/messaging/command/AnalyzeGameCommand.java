package krupkoillia.chesstracker.gameservice.messaging.command;

import java.time.Instant;
import java.util.UUID;

public record AnalyzeGameCommand(
        UUID commandId,
        Long gameId,
        Long userId,
        String pgn,
        Instant occurredAt
) {}
