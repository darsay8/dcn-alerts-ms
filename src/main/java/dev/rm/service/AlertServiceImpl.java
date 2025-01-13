package dev.rm.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.rm.model.Alert;
import dev.rm.repository.AlertRepository;

@Service
@Transactional
public class AlertServiceImpl implements AlertService {

    private AlertRepository alertRepository;

    public AlertServiceImpl(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @Override
    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();

    }

    @Override
    public Alert getAlertById(Long alertId) {
        return alertRepository.findById(alertId).orElseThrow(() -> new RuntimeException("Alert not found"));

    }

}
