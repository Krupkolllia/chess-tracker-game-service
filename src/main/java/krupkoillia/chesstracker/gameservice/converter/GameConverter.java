package krupkoillia.chesstracker.gameservice.converter;

import java.util.ArrayList;
import java.util.List;
import krupkoillia.chesstracker.gameservice.dto.ParsedGame;
import krupkoillia.chesstracker.gameservice.dto.ParsedMove;
import krupkoillia.chesstracker.gameservice.model.GameEntity;
import krupkoillia.chesstracker.gameservice.model.MoveEntity;
import krupkoillia.chesstracker.gameservice.model.enums.Color;
import krupkoillia.chesstracker.gameservice.model.enums.TimeControl;
import org.springframework.stereotype.Component;

@Component
public class GameConverter {

    public GameEntity convert(ParsedGame parsedGame) {
        GameEntity gameEntity = new GameEntity();

        gameEntity
            .setTimeControl(mapTimeControl(parsedGame.timeControlMillis()))
            .setWhiteName(parsedGame.whiteName())
            .setBlackName(parsedGame.blackName())
            .setWhiteElo(parsedGame.whiteElo())
            .setBlackElo(parsedGame.blackElo())
            .setResult(parsedGame.result());

        gameEntity.setMoves(mapMovesToModel(parsedGame.moves(), gameEntity));

        return gameEntity;
    }

    private List<MoveEntity> mapMovesToModel(List<ParsedMove> moves, GameEntity game) {
        List<MoveEntity> moveEntities = new ArrayList<>();

        int plyIndex = 0;
        for (ParsedMove move : moves) {
            MoveEntity moveEntity = new MoveEntity();

            Color moveColor = plyIndex % 2 == 0 ? Color.WHITE : Color.BLACK;
            int moveNumber = plyIndex / 2 + 1;

            moveEntity
                .setGame(game)
                .setMoveNumber(moveNumber)
                .setColor(moveColor)
                .setFen(move.fen())
                .setNotation(move.notation());

            moveEntities.add(moveEntity);
            plyIndex++;
        }

        return moveEntities;
    }

    private TimeControl mapTimeControl(Long millis) {

        if (millis == null || millis <= 0) {
            return TimeControl.UNKNOWN;
        }

        long seconds = millis / 1000;

        if (seconds <= 60) {
            return TimeControl.BULLET;
        }

        if (seconds < 600) {
            return TimeControl.BLITZ;
        }

        if (seconds < 1800) {
            return TimeControl.RAPID;
        }

        return TimeControl.CLASSICAL;
    }

}
