package krupkoillia.chesstracker.gameservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import krupkoillia.chesstracker.gameservice.dto.GameResponseDto;
import krupkoillia.chesstracker.gameservice.dto.UploadGameRequestDto;
import krupkoillia.chesstracker.gameservice.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Game uploading and management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    @Operation(summary = "Upload game from PGN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public GameResponseDto uploadGame(@Valid @RequestBody UploadGameRequestDto requestDto) {
        return gameService.upload(requestDto);
    }

    @Operation(summary = "Get a page of games for specific user")
    @GetMapping
    public Page<GameResponseDto> getAll(@ParameterObject Pageable pageable) {
        return gameService.findAll(pageable);
    }

    @Operation(summary = "Get a game by id and user id")
    @GetMapping("/{id}")
    public GameResponseDto getGameById(@PathVariable Long id) {
        return gameService.findById(id);
    }

    @Operation(summary = "Soft-delete user's game by id")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteGameById(@PathVariable Long id) {
        gameService.deleteById(id);
    }

}
