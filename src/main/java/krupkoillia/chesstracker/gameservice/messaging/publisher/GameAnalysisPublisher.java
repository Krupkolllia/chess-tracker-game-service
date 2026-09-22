package krupkoillia.chesstracker.gameservice.messaging.publisher;

import krupkoillia.chesstracker.gameservice.messaging.command.AnalyzeGameCommand;

public interface GameAnalysisPublisher {

    void publish(AnalyzeGameCommand command);

}
