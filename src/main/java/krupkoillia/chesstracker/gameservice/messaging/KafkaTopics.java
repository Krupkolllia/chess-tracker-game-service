package krupkoillia.chesstracker.gameservice.messaging;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopics {

    public static final String GAME_ANALYSIS_COMMANDS =
            "game-analysis-commands";

    @Bean
    public NewTopic gameAnalysisRequestedTopic() {
        return TopicBuilder.name(GAME_ANALYSIS_COMMANDS)
            .partitions(3)
            .replicas(1)
            .build();
    }

    private KafkaTopics() {

    }

}
