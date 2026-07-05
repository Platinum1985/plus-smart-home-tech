package ru.yandex.practicum;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public String snapshotTopic() {
        return "${spring.kafka.topics.snapshot:telemetry.snapshots.v1}";
    }
}
