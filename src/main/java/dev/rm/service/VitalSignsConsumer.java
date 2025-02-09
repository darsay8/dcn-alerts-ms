package dev.rm.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import dev.rm.model.Alert;
import dev.rm.model.AlertMessage;
import dev.rm.model.VitalSignsMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VitalSignsConsumer {

    private final AlertService alertService;
    private final AlertsProducer alertsProducer;

    @KafkaListener(topics = "vital_signs", groupId = "vitalSignsGroup")
    public void listen(VitalSignsMessage message) {
        log.info("Received vital signs for Patient ID: {}", message.getPatientId());
        log.info("Vital signs: {}", message);

        Alert alert = alertService.generateAlertFromVitalSigns(message);

        if (alert != null) {
            AlertMessage alertMessage = new AlertMessage(
                    alert.getPatient(),
                    alert.getType(),
                    alert.getLevel(),
                    alert.getDescription());
            alertsProducer.sendAlert(alertMessage);
        }
    }
}
