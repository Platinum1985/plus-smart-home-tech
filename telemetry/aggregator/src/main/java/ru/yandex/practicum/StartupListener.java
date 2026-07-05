package ru.yandex.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StartupListener {

    private final AggregationStarter aggregator;

    public StartupListener(AggregationStarter aggregator) {
        this.aggregator = aggregator;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onContextRefreshed() {
        log.info("Контекст Spring инициализирован, запускаем агрегацию");
        aggregator.start();
    }
}