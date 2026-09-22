package krupkoillia.chesstracker.gameservice.messaging.publisher;

import krupkoillia.chesstracker.gameservice.messaging.KafkaTopics;
import krupkoillia.chesstracker.gameservice.messaging.event.AnalyzeGameCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KafkaGameAnalysisPublisher implements GameAnalysisPublisher {

    private final KafkaTemplate<String, AnalyzeGameCommand> kafkaTemplate;

    @Override
    public void publish(AnalyzeGameCommand command) {
        kafkaTemplate.send(KafkaTopics.GAME_ANALYSIS_COMMANDS, command);
    }
}
