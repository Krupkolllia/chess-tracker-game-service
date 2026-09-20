package krupkoillia.chesstracker.gameservice.dto;

import java.util.List;
import krupkoillia.chesstracker.gameservice.model.enums.Result;

public record ParsedGame(
        String whiteName,
        String blackName,
        Integer whiteElo,
        Integer blackElo,
        Long timeControlMillis,
        Result result,
        List<ParsedMove> moves
) {}
