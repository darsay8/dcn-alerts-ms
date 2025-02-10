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
import dev.rm.model.PatientMessage;
import dev.rm.model.PatientStatus;
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
    private final PatientStatusProducer patientStatusProducer;
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

        Patient patient = getPatientDetails(vitalSigns.getPatientId());

        if (patient == null) {
            log.error("Patient with ID {} not found", vitalSigns.getPatientId());
            throw new RuntimeException("Patient not found");
        }

        AlertLevel alertLevel = evaluateVitalSigns(vitalSigns);
        AlertType alertType = getAlertTypeBasedOnLevel(alertLevel);
        String alertDescription = generateAlertDescription(vitalSigns, alertLevel);

        if (alertType == AlertType.CRITICAL || alertType == AlertType.WARNING) {
            return createAlertAndSendPatientUpdate(patient, alertLevel, alertType, alertDescription);
        } else {
            log.info("No alert generated for patient {}. Vital signs are within the normal range.", patient.getName());
            return null;
        }
    }

    private Patient getPatientDetails(Long patientId) {
        return restTemplate.getForObject(patientServiceUrl + "/" + patientId, Patient.class);
    }

    private AlertLevel evaluateVitalSigns(VitalSignsMessage vitalSigns) {
        if (vitalSigns.getHeartRate() > 120 || vitalSigns.getOxygenSaturation() < 90) {
            return AlertLevel.HIGH;
        } else if (vitalSigns.getHeartRate() > 100) {
            return AlertLevel.MEDIUM;
        } else {
            return AlertLevel.LOW;
        }
    }

    private AlertType getAlertTypeBasedOnLevel(AlertLevel alertLevel) {
        if (alertLevel == AlertLevel.HIGH) {
            return AlertType.CRITICAL;
        } else if (alertLevel == AlertLevel.MEDIUM) {
            return AlertType.WARNING;
        } else {
            return AlertType.NORMALIZATION;
        }
    }

    private String generateAlertDescription(VitalSignsMessage vitalSigns, AlertLevel alertLevel) {
        StringBuilder description = new StringBuilder();

        if (vitalSigns.getHeartRate() > 120) {
            description.append("High heart rate detected: ").append(vitalSigns.getHeartRate()).append(" bpm. ");
        } else if (vitalSigns.getHeartRate() > 100) {
            description.append("Moderate heart rate detected: ").append(vitalSigns.getHeartRate()).append(" bpm. ");
        }

        if (vitalSigns.getOxygenSaturation() < 90) {
            description.append("Low oxygen saturation: ").append(vitalSigns.getOxygenSaturation()).append("%. ");
        }

        if (vitalSigns.getBloodPressure() != null) {
            String[] bp = vitalSigns.getBloodPressure().split("/");
            int systolic = Integer.parseInt(bp[0]);
            int diastolic = Integer.parseInt(bp[1]);

            if (systolic > 180 || diastolic > 120) {
                description.append("Critical blood pressure: ").append(vitalSigns.getBloodPressure()).append(". ");
            } else if (systolic > 140 || diastolic > 90) {
                description.append("Elevated blood pressure: ").append(vitalSigns.getBloodPressure()).append(". ");
            }
        }

        if (vitalSigns.getGlucose() != null && vitalSigns.getGlucose().intValue() > 200) {
            description.append("High glucose level: ").append(vitalSigns.getGlucose()).append(" mg/dL. ");
        }

        switch (alertLevel) {
            case HIGH:
                description.append("This is a critical condition.");
                break;
            case MEDIUM:
                description.append("This is a moderate concern.");
                break;
            case LOW:
                description.append("Vital signs are within normal ranges.");
                break;
            default:
                description.append("No alert necessary.");
                break;
        }

        return description.toString();
    }

    private Alert createAlertAndSendPatientUpdate(Patient patient, AlertLevel alertLevel, AlertType alertType,
            String description) {

        Alert alert = Alert.builder()
                .patient(patient.getName())
                .type(alertType)
                .level(alertLevel)
                .description(description)
                .build();

        alert = alertRepository.save(alert);
        sendPatientStatusUpdate(patient, alertLevel);
        return alert;
    }

    private void sendPatientStatusUpdate(Patient patient, AlertLevel alertLevel) {
        PatientStatus status = (alertLevel == AlertLevel.HIGH) ? PatientStatus.CRITICAL : PatientStatus.SERIOUS;

        PatientMessage patientMessage = new PatientMessage(
                String.valueOf(patient.getPatientId()),
                status);

        patientStatusProducer.sendPatientStatus(patientMessage);

        log.info("Patient status update sent for Patient ID: {} with status: {}", patient.getPatientId(), status);
    }

}
