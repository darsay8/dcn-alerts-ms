package dev.rm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import dev.rm.model.Alert;
import dev.rm.model.VitalSignsMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertConsumer {

    private final AlertService alertService;

    @RabbitListener(queues = "alertQueue")
    public void receiveVitalSigns(VitalSignsMessage message) {
        log.info("Received vital signs for Patient ID: {}", message.getPatientId());

        Alert alert = alertService.generateAlertFromVitalSigns(message);
        log.info("Generated Alert: {}", alert);
    }
}