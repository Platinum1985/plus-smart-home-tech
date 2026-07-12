package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.processor.HubEventProcessor;
import ru.yandex.practicum.processor.SnapshotProcessor;

@EnableDiscoveryClient
@SpringBootApplication
@ConfigurationPropertiesScan
public class Analyzer {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Analyzer.class, args);

        SnapshotProcessor snapshotProcessor = context.getBean(SnapshotProcessor.class);
        HubEventProcessor hubEventProcessor = context.getBean(HubEventProcessor.class);

        Thread hubThread = new Thread(hubEventProcessor);
        hubThread.setName("HubEventProcessor");
        hubThread.start();

        snapshotProcessor.run();
    }
}