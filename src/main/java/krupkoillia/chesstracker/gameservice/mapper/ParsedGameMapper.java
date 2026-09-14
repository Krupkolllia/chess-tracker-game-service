package krupkoillia.chesstracker.gameservice.mapper;

import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.game.GameResult;
import com.github.bhlangonijr.chesslib.game.TimeControlType;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;
import java.util.ArrayList;
import java.util.List;
import krupkoillia.chesstracker.gameservice.exception.InvalidGameResultTypeException;
import krupkoillia.chesstracker.gameservice.model.GameEntity;
import krupkoillia.chesstracker.gameservice.model.MoveEntity;
import krupkoillia.chesstracker.gameservice.model.enums.Color;
import krupkoillia.chesstracker.gameservice.model.enums.Result;
import krupkoillia.chesstracker.gameservice.model.enums.TimeControl;
import org.springframework.stereotype.Component;

@Component
public class ParsedGameMapper {

    public GameEntity toModel(Game parsedGame) {
        GameEntity gameEntity = new GameEntity();

        gameEntity
                .setTimeControl(mapTimeControl(parsedGame))
                .setWhiteName(parsedGame.getWhitePlayer().getName())
                .setBlackName(parsedGame.getBlackPlayer().getName())
                .setWhiteElo(parsedGame.getWhitePlayer().getElo())
                .setBlackElo(parsedGame.getBlackPlayer().getElo())
                .setResult(mapResult(parsedGame.getResult()));

        gameEntity.setMoves(mapMovesToModel(parsedGame.getHalfMoves(), gameEntity));

        return gameEntity;
    }

    private List<MoveEntity> mapMovesToModel(MoveList moves, GameEntity game) {
        List<MoveEntity> moveEntities = new ArrayList<>();

        int plyIndex = 0;
        for (Move move : moves) {
            MoveEntity moveEntity = new MoveEntity();

            Color moveColor = plyIndex % 2 == 0 ? Color.WHITE : Color.BLACK;
            int moveNumber = plyIndex / 2 + 1;

            moveEntity
                    .setGame(game)
                    .setMoveNumber(moveNumber)
                    .setColor(moveColor)
                    .setFen(moves.getFen(plyIndex, false))
                    .setNotation(move.getSan());

            moveEntities.add(moveEntity);
            plyIndex++;
        }

        return moveEntities;
    }

    private TimeControl mapTimeControl(Game game) {
        com.github.bhlangonijr.chesslib.game.TimeControl timeControl =
                game.getRound().getEvent().getTimeControl();

        if (timeControl.getTimeControlType() == TimeControlType.UNKNOW) {
            return TimeControl.UNKNOWN;
        }

        long seconds = timeControl.getMilliseconds() / 1000;

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

    private Result mapResult(GameResult gameResult) {
        return switch (gameResult.value()) {
            case "WHITE_WON" -> Result.WHITE_WON;
            case "BLACK_WON" -> Result.BLACK_WON;
            case "DRAW" -> Result.DRAW;
            default -> throw new InvalidGameResultTypeException(
                "Unsupported game result: " + gameResult.value());
        };
    }

}
