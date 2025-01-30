package dev.rm.service;

import java.util.List;

import dev.rm.model.Alert;
import dev.rm.model.VitalSignsMessage;

public interface AlertService {
    List<Alert> getAllAlerts();

    Alert getAlertById(Long alertId);

    Alert createAlert(Alert alert);

    Alert updateAlert(Long alertId, Alert updatedAlert);

    void deleteAlert(Long alertId);

    Alert generateAlertFromVitalSigns(VitalSignsMessage vitalSigns);

}
