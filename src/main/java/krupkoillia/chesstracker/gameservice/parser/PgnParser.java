package krupkoillia.chesstracker.gameservice.parser;

import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.pgn.PgnIterator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import krupkoillia.chesstracker.gameservice.exception.InvalidPgnException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PgnParser {

    public Game parseGame(String pgn) {
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

    private Game parseFile(Path file) {
        try (PgnIterator games = new PgnIterator(file.toString())) {
            Iterator<Game> iterator = games.iterator();

            if (!iterator.hasNext()) {
                throw new InvalidPgnException("Provided PGN contains no games");
            }

            Game game = iterator.next();

            if (game == null) {
                throw new InvalidPgnException("Chesslib returned null game");
            }

            return game;
        } catch (InvalidPgnException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to parse PGN file: {}", file, e);
            throw new InvalidPgnException("Provided PGN is invalid", e);
        }
    }

    private void deleteTempFile(Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            log.warn("Failed to delete temporary PGN file: {}", file, e);
        }
    }
}
