package dev.rm.init;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import dev.rm.factory.AlertFactory;
import dev.rm.model.Alert;
import dev.rm.model.AlertLevel;
import dev.rm.model.AlertType;
import dev.rm.repository.AlertRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    private final AlertRepository alertRepository;

    public DataInitializer(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Initializing data...");

        Alert alert1 = AlertFactory.createAlert("Ethan Carter", AlertType.WARNING,
                AlertLevel.MEDIUM,
                "Elevated blood pressure: 130/80. This is a moderate concern.");

        Alert alert2 = AlertFactory.createAlert("Lila Bennett", AlertType.CRITICAL,
                AlertLevel.HIGH,
                "Moderate heart rate detected: 132 bpm. This is a critical concern.");

        alertRepository.saveAll(Arrays.asList(alert1, alert2));

    }

}
