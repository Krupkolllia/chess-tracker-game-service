package krupkoillia.chesstracker.gameservice.parser;

import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.game.GameResult;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.github.bhlangonijr.chesslib.pgn.PgnIterator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import krupkoillia.chesstracker.gameservice.dto.ParsedGame;
import krupkoillia.chesstracker.gameservice.dto.ParsedMove;
import krupkoillia.chesstracker.gameservice.exception.InvalidGameResultTypeException;
import krupkoillia.chesstracker.gameservice.exception.InvalidPgnException;
import krupkoillia.chesstracker.gameservice.model.enums.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ChesslibPgnParser implements PgnParser {

    public ParsedGame parse(String pgn) {
        Path tempFile = createTempFile(pgn);

        try {
            return parseFile(tempFile);
        } finally {
            deleteTempFile(tempFile);
        }
    }

    private Path createTempFile(String pgn) {
        try {
            Path tempFile = Files.createTempFile("game-", ".pgn");
            Files.writeString(tempFile, pgn);
            return tempFile;
        } catch (IOException e) {
            log.error("Failed to create temporary PGN file", e);
            throw new InvalidPgnException("Could not create temporary PGN file", e);
        }
    }

    private ParsedGame parseFile(Path file) {
        try (PgnIterator games = new PgnIterator(file.toString())) {
            Iterator<Game> iterator = games.iterator();

            if (!iterator.hasNext()) {
                throw new InvalidPgnException("Provided PGN contains no games");
            }

            Game game = iterator.next();

            if (game == null) {
                throw new InvalidPgnException("Chesslib returned null game");
            }

            return toDto(game);
        } catch (InvalidPgnException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to parse PGN file: {}", file, e);
            throw new InvalidPgnException("Provided PGN is invalid", e);
        }
    }

    private ParsedGame toDto(Game game) {
        return new ParsedGame(
            game.getWhitePlayer().getName(),
            game.getBlackPlayer().getName(),
            game.getWhitePlayer().getElo(),
            game.getBlackPlayer().getElo(),
            game.getRound().getEvent().getTimeControl().getMilliseconds(),
            mapGameResult(game.getResult()),
            mapMoves(game.getHalfMoves())
        );
    }

    private Result mapGameResult(GameResult gameResult) {
        return switch (gameResult) {
            case GameResult.WHITE_WON -> Result.WHITE_WON;
            case GameResult.BLACK_WON -> Result.BLACK_WON;
            case GameResult.DRAW -> Result.DRAW;

            default -> throw new InvalidGameResultTypeException(
                    "Not supported game result type: " + gameResult.name());
        };
    }

    private List<ParsedMove> mapMoves(MoveList moves) {
        List<ParsedMove> parsedMoves = new ArrayList<>();

        for (int i = 1; i < moves.size(); i++) {
            String fen = moves.getFen(i, false);
            String notation = moves.get(i - 1).getSan();

            parsedMoves.add(new ParsedMove(fen, notation));
        }

        return parsedMoves;
    }

    private void deleteTempFile(Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            log.warn("Failed to delete temporary PGN file: {}", file, e);
        }
    }
}
