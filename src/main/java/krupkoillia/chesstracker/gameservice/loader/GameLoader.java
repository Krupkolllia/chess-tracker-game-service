package krupkoillia.chesstracker.gameservice.loader;

import krupkoillia.chesstracker.gameservice.converter.GameConverter;
import krupkoillia.chesstracker.gameservice.model.GameEntity;
import krupkoillia.chesstracker.gameservice.parser.PgnParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GameLoader {

    private final PgnParser pgnParser;

    private final GameConverter gameConverter;

    public GameEntity load(String pgn) {
        return gameConverter.convert(pgnParser.parse(pgn));
    }

}
