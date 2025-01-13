package dev.rm.service;

import java.util.List;

import dev.rm.model.Alert;

public interface AlertService {

    List<Alert> getAllAlerts();

    Alert getAlertById(Long alertId);

}
