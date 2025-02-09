package dev.rm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import dev.rm.model.Alert;
import dev.rm.model.AlertMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertConsumer {

    private final AlertService alertService;

    @KafkaListener(topics = "alerts", groupId = "alertsGroup")
    public void listen(AlertMessage message) {
        log.info("Received alert for Patient: {}", message.getPatientName());
        log.info("Alert: {}", message);

        Alert alert = Alert.builder()
                .patient(message.getPatientName())
                .type(message.getType())
                .level(message.getLevel())
                .description(message.getDescription())
                .build();

        alertService.createAlert(alert);
    }
}
