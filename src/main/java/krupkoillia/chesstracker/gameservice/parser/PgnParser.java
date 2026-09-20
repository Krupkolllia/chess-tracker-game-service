package krupkoillia.chesstracker.gameservice.parser;

import krupkoillia.chesstracker.gameservice.dto.ParsedGame;

public interface PgnParser {

    ParsedGame parse(String pgn);

}
