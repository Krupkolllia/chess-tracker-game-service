package krupkoillia.chesstracker.gameservice.service;

import com.github.bhlangonijr.chesslib.game.Game;
import krupkoillia.chesstracker.gameservice.dto.GameResponseDto;
import krupkoillia.chesstracker.gameservice.dto.UploadGameRequestDto;
import krupkoillia.chesstracker.gameservice.exception.EntityNotFoundException;
import krupkoillia.chesstracker.gameservice.mapper.GameMapper;
import krupkoillia.chesstracker.gameservice.mapper.ParsedGameMapper;
import krupkoillia.chesstracker.gameservice.model.GameEntity;
import krupkoillia.chesstracker.gameservice.model.enums.AnalysisStatus;
import krupkoillia.chesstracker.gameservice.parser.PgnParser;
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

    private final PgnParser pgnParser;

    private final ParsedGameMapper parsedGameMapper;

    @Transactional(readOnly = true)
    public Page<GameResponseDto> findAllByUserId(Pageable pageable) {
        Long userId = SecurityUtil.getAuthenticatedUserId();

        return gameRepository.findAllByUserId(userId, pageable)
            .map(gameMapper::toDto);
    }

    @Transactional(readOnly = true)
    public GameResponseDto findByIdAndUserId(Long id) {
        Long userId = SecurityUtil.getAuthenticatedUserId();

        GameEntity game = gameRepository.findByIdAndUserId(id, userId).orElseThrow(
                () -> new EntityNotFoundException(
                    "Cannot find a game with id " + id + " for current user")
        );

        return gameMapper.toDto(game);
    }

    @Transactional
    public GameResponseDto uploadGame(UploadGameRequestDto requestDto) {
        Game parsedGame = pgnParser.parseGame(requestDto.pgn());
        GameEntity mappedGame = parsedGameMapper.toModel(parsedGame);

        Long userId = SecurityUtil.getAuthenticatedUserId();

        mappedGame
                .setUserId(userId)
                .setPgn(requestDto.pgn())
                .setUserColor(requestDto.userColor())
                .setAnalysisStatus(AnalysisStatus.NOT_STARTED);

        gameRepository.saveAndFlush(mappedGame);

        return gameMapper.toDto(mappedGame);

    }

    @Transactional
    public void deleteByIdAndUserId(Long id) {
        Long userId = SecurityUtil.getAuthenticatedUserId();

        if (!gameRepository.existsByIdAndUserId(id, userId)) {
            throw new EntityNotFoundException("Cannot delete not existing game");
        }

        gameRepository.deleteByIdAndUserId(id, userId);
    }

}
