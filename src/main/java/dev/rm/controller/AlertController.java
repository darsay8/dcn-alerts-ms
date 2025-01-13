package dev.rm.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.rm.model.Alert;
import dev.rm.service.AlertService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<Alert>> getAllAlerts() {
        log.info("Getting all alerts");
        List<Alert> alerts = alertService.getAllAlerts();

        if (alerts.isEmpty()) {
            log.info("No alerts found");
            return ResponseEntity.noContent().build();
        } else {
            log.info("Alerts found");
            return ResponseEntity.ok(alerts);
        }
    }

    @GetMapping("/alerts/{id}")
    public ResponseEntity<Alert> getAlertById(@PathVariable Long id) {
        log.info("Getting alert by id: {}", id);

        try {
            Alert alert = alertService.getAlertById(id);
            log.info("Alert found: {}", alert);
            return ResponseEntity.ok(alert);
        } catch (RuntimeException e) {
            log.error("Alert not found", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

}
