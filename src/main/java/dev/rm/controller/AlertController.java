package dev.rm.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.rm.model.Alert;
import dev.rm.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping
    public ResponseEntity<List<Alert>> getAllAlerts() {
        log.info("Getting all alerts...");
        List<Alert> alerts = alertService.getAllAlerts();
        return alerts.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(alerts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alert> getAlertById(@PathVariable Long id) {
        log.info("Getting alert by ID: {}", id);
        try {
            return ResponseEntity.ok(alertService.getAlertById(id));
        } catch (RuntimeException e) {
            log.error("Error retrieving alert: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Alert> createAlert(@RequestBody Alert alert) {
        log.info("Creating a new alert...");
        Alert createdAlert = alertService.createAlert(alert);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAlert);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Alert> updateAlert(@PathVariable Long id, @RequestBody Alert updatedAlert) {
        log.info("Updating alert with ID: {}", id);
        try {
            Alert alert = alertService.updateAlert(id, updatedAlert);
            return ResponseEntity.ok(alert);
        } catch (RuntimeException e) {
            log.error("Error updating alert: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAlert(@PathVariable Long id) {
        log.info("Deleting alert with ID: {}", id);
        try {
            alertService.deleteAlert(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting alert: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}