package krupkoillia.chesstracker.gameservice.service;

import java.time.Instant;
import java.util.UUID;
import krupkoillia.chesstracker.gameservice.dto.GameResponseDto;
import krupkoillia.chesstracker.gameservice.dto.UploadGameRequestDto;
import krupkoillia.chesstracker.gameservice.exception.EntityNotFoundException;
import krupkoillia.chesstracker.gameservice.loader.GameLoader;
import krupkoillia.chesstracker.gameservice.mapper.GameMapper;
import krupkoillia.chesstracker.gameservice.model.GameEntity;
import krupkoillia.chesstracker.gameservice.model.enums.AnalysisStatus;
import krupkoillia.chesstracker.gameservice.repository.GameRepository;
import krupkoillia.chesstracker.gameservice.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    private final GameMapper gameMapper;

    private final GameLoader gameLoader;

    private final GameAnalysisPublisher publisher;

    @Transactional(readOnly = true)
    public Page<GameResponseDto> findAll(Pageable pageable) {
        Long userId = SecurityUtil.getAuthenticatedUserId();

        return gameRepository.findAllByUserId(userId, pageable)
            .map(gameMapper::toDto);
    }

    @Transactional(readOnly = true)
    public GameResponseDto findById(Long id) {
        Long userId = SecurityUtil.getAuthenticatedUserId();

        GameEntity game = gameRepository.findByIdAndUserId(id, userId).orElseThrow(
                () -> new EntityNotFoundException(
                    "Cannot find a game with id " + id + " for current user")
        );

        return gameMapper.toDto(game);
    }

    @Transactional
    public GameResponseDto upload(UploadGameRequestDto requestDto) {
        GameEntity game = gameLoader.load(requestDto.pgn());

        Long userId = SecurityUtil.getAuthenticatedUserId();

        game
                .setUserId(userId)
                .setPgn(requestDto.pgn())
                .setUserColor(requestDto.userColor())
                .setAnalysisStatus(AnalysisStatus.NOT_REQUESTED);

        gameRepository.saveAndFlush(game);

        return gameMapper.toDto(game);

    }

    @Transactional
    public void requestAnalysis(Long gameId) {
        Long userId = SecurityUtil.getAuthenticatedUserId();

        GameEntity game = gameRepository.findByIdAndUserId(gameId, userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Cannot find the game to analyze"));

        AnalyzeGameCommand command = new AnalyzeGameCommand(
                UUID.randomUUID(),
                gameId,
                userId,
                game.getPgn(),
                Instant.now()
        );

        publisher.publish(command);

        game.setAnalysisStatus(AnalysisStatus.REQUESTED);
    }

    @Transactional
    public void deleteById(Long id) {
        Long userId = SecurityUtil.getAuthenticatedUserId();

        if (!gameRepository.existsByIdAndUserId(id, userId)) {
            throw new EntityNotFoundException("Cannot delete not existing game");
        }

        gameRepository.deleteByIdAndUserId(id, userId);
    }

}
