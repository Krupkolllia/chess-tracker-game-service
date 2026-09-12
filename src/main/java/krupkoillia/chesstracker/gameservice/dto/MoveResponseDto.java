package krupkoillia.chesstracker.gameservice.dto;

import krupkoillia.chesstracker.gameservice.model.enums.Color;
import lombok.With;

@With
public record MoveResponseDto(
        Long id,
        Integer moveNumber,
        Color color,
        String fen,
        String notation
) {}
