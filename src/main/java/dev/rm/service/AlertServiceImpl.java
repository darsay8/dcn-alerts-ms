package dev.rm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import dev.rm.model.Alert;
import dev.rm.model.AlertLevel;
import dev.rm.model.AlertType;
import dev.rm.model.Patient;
import dev.rm.model.VitalSignsMessage;
import dev.rm.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final RestTemplate restTemplate;

    @Value("${service.patient.url}")
    private String patientServiceUrl;

    @Override
    public List<Alert> getAllAlerts() {
        log.info("Fetching all alerts...");
        return alertRepository.findAll();
    }

    @Override
    public Alert getAlertById(Long alertId) {
        log.info("Fetching alert by ID: {}", alertId);
        return alertRepository.findById(alertId)
                .orElseThrow(() -> {
                    log.error("Alert not found with ID: {}", alertId);
                    return new RuntimeException("Alert not found");
                });
    }

    @Override
    public Alert createAlert(Alert alert) {
        log.info("Creating new alert...");
        return alertRepository.save(alert);
    }

    @Override
    public Alert updateAlert(Long alertId, Alert updatedAlert) {
        return alertRepository.findById(alertId).map(alert -> {
            log.info("Updating alert with ID: {}", alertId);
            alert.setPatient(updatedAlert.getPatient());
            alert.setType(updatedAlert.getType());
            alert.setLevel(updatedAlert.getLevel());
            alert.setDescription(updatedAlert.getDescription());
            return alertRepository.save(alert);
        }).orElseThrow(() -> {
            log.error("Alert not found with ID: {}", alertId);
            return new RuntimeException("Alert not found");
        });
    }

    @Override
    public void deleteAlert(Long alertId) {
        if (alertRepository.existsById(alertId)) {
            log.info("Deleting alert with ID: {}", alertId);
            alertRepository.deleteById(alertId);
        } else {
            log.error("Alert not found with ID: {}", alertId);
            throw new RuntimeException("Alert not found");
        }
    }

    @Override
    public Alert generateAlertFromVitalSigns(VitalSignsMessage vitalSigns) {

        Patient patient = restTemplate.getForObject(
                patientServiceUrl + "/" + vitalSigns.getPatientId(), Patient.class);

        if (patient == null) {
            log.error("Patient with ID {} not found", vitalSigns.getPatientId());
            throw new RuntimeException("Patient not found");
        }

        AlertLevel level;
        AlertType type;
        String description;

        if (vitalSigns.getHeartRate() > 120 || vitalSigns.getOxygenSaturation() < 90) {
            level = AlertLevel.HIGH;
            type = AlertType.CRITICAL;
            description = "Critical vital signs detected!";
        } else if (vitalSigns.getHeartRate() > 100) {
            level = AlertLevel.MEDIUM;
            type = AlertType.WARNING;
            description = "Warning: Elevated heart rate.";
        } else {
            level = AlertLevel.LOW;
            type = AlertType.NORMALIZATION;
            description = "Vital signs are normal.";
        }

        if (type == AlertType.CRITICAL || type == AlertType.WARNING) {

            log.info("Alert generated for patient: {} with level: {}", patient.getName(), level);

            Alert alert = Alert.builder()
                    .patient(patient.getName())
                    .type(type)
                    .level(level)
                    .description(description)
                    .build();

            return alertRepository.save(alert);
        } else {
            log.info("No alert saved for patient: {} as vital signs are within normal range", patient.getName());
            return null;
        }
    }

}
