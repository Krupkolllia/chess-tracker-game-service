package krupkoillia.chesstracker.gameservice.dto;

import krupkoillia.chesstracker.gameservice.model.enums.Color;

public record MoveResponseDto(
        Long id,
        Integer moveNumber,
        Color color,
        String fen,
        String notation
) {}
