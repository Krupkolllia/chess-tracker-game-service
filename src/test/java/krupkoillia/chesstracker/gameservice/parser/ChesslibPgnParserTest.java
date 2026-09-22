package krupkoillia.chesstracker.gameservice.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import krupkoillia.chesstracker.gameservice.dto.ParsedGame;
import krupkoillia.chesstracker.gameservice.exception.InvalidPgnException;
import krupkoillia.chesstracker.gameservice.model.enums.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ChesslibPgnParserTest {

    private final PgnParser pgnParser = new ChesslibPgnParser();

    @Test
    @DisplayName("""
            parse method with valid pgn should
            return parsed game
            """)
    void parse_WithValidPgn_ShouldReturnParsedGame() {
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
        ParsedGame actual = pgnParser.parse(pgn);

        // Then
        assertThat(actual.timeControlMillis())
                .isEqualTo(300_000L);

        assertThat(actual.whiteName())
                .isEqualTo("Magnus Carlsen");

        assertThat(actual.blackName())
                .isEqualTo("Hikaru Nakamura");

        assertThat(actual.whiteElo())
                .isEqualTo(3380);

        assertThat(actual.blackElo())
                .isEqualTo(3407);

        assertThat(actual.result()).isEqualTo(Result.DRAW);

    }

    @Test
    @DisplayName("""
            parse method with pgn without game result
            should throw InvalidPgnException
            """)
    void parse_WithPgnWithoutGameResult_ShouldThrowInvalidPgnException() {
        // Given
        String pgn = """
                    [White "Magnus Carlsen"]
                    [Black "Hikaru Nakamura"]
                    [WhiteElo "3380"]
                    [BlackElo "3407"]
                    [TimeControl "300"]
                    1. e4 e5 2. Nf3 Nc6 3. Bb5 a6 1/2-1/2
                    """;

        assertThatThrownBy(() -> pgnParser.parse(pgn))
                .isExactlyInstanceOf(InvalidPgnException.class)
                .hasMessage("Game result is missing in provided PGN");

    }

    @Test
    @DisplayName("""
            parse method with pgn without time control
            should throw InvalidPgnException
            """)
    void parse_WithPgnWithoutTimeControl_ShouldThrowInvalidPgnException() {
        // Given
        String pgn = """
                    [White "Magnus Carlsen"]
                    [Black "Hikaru Nakamura"]
                    [Result "1/2-1/2"]
                    [WhiteElo "3380"]
                    [BlackElo "3407"]
                    1. e4 e5 2. Nf3 Nc6 3. Bb5 a6 1/2-1/2
                    """;

        assertThatThrownBy(() -> pgnParser.parse(pgn))
                .isExactlyInstanceOf(InvalidPgnException.class)
                .hasMessage("Time control is missing in provided PGN");

    }

    @Test
    @DisplayName("""
            parse method with pgn without moves
            should throw InvalidPgnException
            """)
    void parse_WithPgnWithoutMoves_ShouldThrowInvalidPgnException() {
        // Given
        String pgn = """
                    [White "Magnus Carlsen"]
                    [Black "Hikaru Nakamura"]
                    [Result "1/2-1/2"]
                    [WhiteElo "3380"]
                    [BlackElo "3407"]
                    [TimeControl "300"]
                    """;

        assertThatThrownBy(() -> pgnParser.parse(pgn))
                .isExactlyInstanceOf(InvalidPgnException.class)
                .hasMessage("Moves syntax is invalid");

    }

    @Test
    @DisplayName("""
            parse method with pgn containing invalid moves syntax
            should throw InvalidPgnException
            """)
    void parse_WithPgnContainingInvalidMovesSyntax_ShouldThrowInvalidPgnException() {
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

        assertThatThrownBy(() -> pgnParser.parse(pgn))
                .isExactlyInstanceOf(InvalidPgnException.class);

    }

    @Test
    @DisplayName("""
            parse method with pgn without game
            should throw InvalidPgnException
            """)
    void parse_WithPgnWithoutGame_ShouldThrowInvalidPgnException() {
        // Given
        String pgn = " ";

        assertThatThrownBy(() -> pgnParser.parse(pgn))
                .isExactlyInstanceOf(InvalidPgnException.class)
                .hasMessage("Provided PGN contains no game");

    }

    @Test
    @DisplayName("""
            parse method with pgn containing multiple games
            should throw InvalidPgnException
            """)
    void parse_WithPgnContainingMultipleGames_ShouldThrowInvalidPgnException() {
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

        assertThatThrownBy(() -> pgnParser.parse(pgn))
                .isExactlyInstanceOf(InvalidPgnException.class)
                .hasMessage("Provided PGN contains multiple games");

    }

}
