package krupkoillia.chesstracker.gameservice.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.game.GameResult;
import krupkoillia.chesstracker.gameservice.exception.InvalidPgnException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PgnParserTest {

    private final PgnParser pgnParser = new PgnParser();

    @Test
    @DisplayName("""
            parseGame method with valid pgn should
            return parsed game
            """)
    void parseGame_WithValidPgn_ShouldReturnParsedGame() {
        // Given
        String pgn = """
                    [White "Magnus Carlsen"]
                    [Black "Hikaru Nakamura"]
                    [Result "1/2-1/2"]
                    [WhiteElo "3380"]
                    [BlackElo "3407"]
                    [TimeControl "300"]
                    1. e4 e5 2. Nf3 Nc6 3. Bb5 a6 1/2-1/2
                    """;

        // When
        Game actual = pgnParser.parseGame(pgn);

        // Then
        assertThat(actual.getRound().getEvent().getTimeControl().getMilliseconds())
                .isEqualTo(300_000L);

        assertThat(actual.getWhitePlayer().getName())
                .isEqualTo("Magnus Carlsen");

        assertThat(actual.getBlackPlayer().getName())
                .isEqualTo("Hikaru Nakamura");

        assertThat(actual.getWhitePlayer().getElo())
                .isEqualTo(3380);

        assertThat(actual.getBlackPlayer().getElo())
                .isEqualTo(3407);

        assertThat(actual.getResult()).isEqualTo(GameResult.DRAW);

    }

    @Test
    @DisplayName("""
            parseGame method with pgn without game result
            should throw InvalidPgnException
            """)
    void parseGame_WithPgnWithoutGameResult_ShouldThrowInvalidPgnException() {
        // Given
        String pgn = """
                    [White "Magnus Carlsen"]
                    [Black "Hikaru Nakamura"]
                    [WhiteElo "3380"]
                    [BlackElo "3407"]
                    [TimeControl "300"]
                    1. e4 e5 2. Nf3 Nc6 3. Bb5 a6 1/2-1/2
                    """;

        assertThatThrownBy(() -> pgnParser.parseGame(pgn))
                .isExactlyInstanceOf(InvalidPgnException.class)
                .hasMessage("Game result is missing in provided PGN");

    }

    @Test
    @DisplayName("""
            parseGame method with pgn without time control
            should throw InvalidPgnException
            """)
    void parseGame_WithPgnWithoutTimeControl_ShouldThrowInvalidPgnException() {
        // Given
        String pgn = """
                    [White "Magnus Carlsen"]
                    [Black "Hikaru Nakamura"]
                    [Result "1/2-1/2"]
                    [WhiteElo "3380"]
                    [BlackElo "3407"]
                    1. e4 e5 2. Nf3 Nc6 3. Bb5 a6 1/2-1/2
                    """;

        assertThatThrownBy(() -> pgnParser.parseGame(pgn))
                .isExactlyInstanceOf(InvalidPgnException.class)
                .hasMessage("Time control is missing in provided PGN");

    }

    @Test
    @DisplayName("""
            parseGame method with pgn without moves
            should throw InvalidPgnException
            """)
    void parseGame_WithPgnWithoutMoves_ShouldThrowInvalidPgnException() {
        // Given
        String pgn = """
                    [White "Magnus Carlsen"]
                    [Black "Hikaru Nakamura"]
                    [Result "1/2-1/2"]
                    [WhiteElo "3380"]
                    [BlackElo "3407"]
                    [TimeControl "300"]
                    """;

        assertThatThrownBy(() -> pgnParser.parseGame(pgn))
                .isExactlyInstanceOf(InvalidPgnException.class)
                .hasMessage("Moves syntax is invalid");

    }

    @Test
    @DisplayName("""
            parseGame method with pgn containing invalid moves syntax
            should throw InvalidPgnException
            """)
    void parseGame_WithPgnContainingInvalidMovesSyntax_ShouldThrowInvalidPgnException() {
        // Given
        String pgn = """
                    [White "Magnus Carlsen"]
                    [Black "Hikaru Nakamura"]
                    [Result "1/2-1/2"]
                    [WhiteElo "3380"]
                    [BlackElo "3407"]
                    [TimeControl "300"]
                    1. e4 eee5
                    """;

        assertThatThrownBy(() -> pgnParser.parseGame(pgn))
                .isExactlyInstanceOf(InvalidPgnException.class);

    }

    @Test
    @DisplayName("""
            parseGame method with pgn without game
            should throw InvalidPgnException
            """)
    void parseGame_WithPgnWithoutGame_ShouldThrowInvalidPgnException() {
        // Given
        String pgn = " ";

        assertThatThrownBy(() -> pgnParser.parseGame(pgn))
                .isExactlyInstanceOf(InvalidPgnException.class)
                .hasMessage("Provided PGN contains no game");

    }

    @Test
    @DisplayName("""
            parseGame method with pgn containing multiple games
            should throw InvalidPgnException
            """)
    void parseGame_WithPgnContainingMultipleGames_ShouldThrowInvalidPgnException() {
        // Given
        String pgn = """
                    [White "Magnus Carlsen"]
                    [Black "Hikaru Nakamura"]
                    [Result "1/2-1/2"]
                    [WhiteElo "3380"]
                    [BlackElo "3407"]
                    [TimeControl "300"]
                    1. e4 e5 2. Nf3 Nc6 3. Bb5 a6 1/2-1/2
        
                    [White "Garry Kasparov"]
                    [Black "Bobby Fischer"]
                    [Result "1-0"]
                    [WhiteElo "2851"]
                    [BlackElo "2785"]
                    [TimeControl "300"]
                    1. e4 e5 2. Bc4 Nc6 3. Qh5 Nf6 4. Qxf7# 1-0
                    """;

        assertThatThrownBy(() -> pgnParser.parseGame(pgn))
                .isExactlyInstanceOf(InvalidPgnException.class)
                .hasMessage("Provided PGN contains multiple games");

    }

}
