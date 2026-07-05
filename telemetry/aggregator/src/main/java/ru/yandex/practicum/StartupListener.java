package ru.yandex.practicum;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupListener {

    private final AggregationStarter aggregator;

    // Внедряем зависимость через конструктор — Spring сам подставит бин
    public StartupListener(AggregationStarter aggregator) {
        this.aggregator = aggregator;
    }

    /**
     * Этот метод сработает, когда Spring полностью создаст контекст (событие ContextRefreshedEvent).
     * Тогда мы запускаем основную логику сервиса.
     */
    @EventListener(ContextRefreshedEvent.class)
    public void onContextRefreshed() {
        aggregator.start();
    }
}
