package krupkoillia.chesstracker.gameservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import krupkoillia.chesstracker.gameservice.model.enums.Color;

public record UploadGameRequestDto(
        @NotBlank
        @Size(max = 100_000)
        String pgn,

        @NotNull
        Color userColor
) {}
