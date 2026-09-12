package krupkoillia.chesstracker.gameservice.dto;

import java.time.Instant;
import java.util.List;
import krupkoillia.chesstracker.gameservice.model.enums.AnalysisStatus;
import krupkoillia.chesstracker.gameservice.model.enums.Color;
import krupkoillia.chesstracker.gameservice.model.enums.Result;
import krupkoillia.chesstracker.gameservice.model.enums.TimeControl;
import lombok.With;

@With
public record GameResponseDto(
        Long id,
        String pgn,
        TimeControl timeControl,
        String whiteName,
        String blackName,
        Integer whiteElo,
        Integer blackElo,
        Color userColor,
        Result result,
        List<MoveResponseDto> moves,
        AnalysisStatus analysisStatus,
        Instant createdAt
) {}
