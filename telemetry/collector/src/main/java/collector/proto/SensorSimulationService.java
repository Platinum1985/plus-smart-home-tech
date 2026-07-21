package collector.proto;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SensorSimulationService {

    @Autowired
    private EventDataProducer eventDataProducer;

    @PostConstruct
    public void startSimulation() {
        // Запуск в отдельном потоке
        new Thread(() -> {
            while (true) {
                try {
                    // Генерация и отправка событий
                    // ... создать события для всех датчиков
                    Thread.sleep(5000); // каждые 5 секунд
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }
}
