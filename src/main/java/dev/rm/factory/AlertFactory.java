package dev.rm.factory;

import dev.rm.model.Alert;
import dev.rm.model.AlertLevel;
import dev.rm.model.AlertType;

public class AlertFactory {
    public static Alert createAlert(String patientName, AlertType type, AlertLevel level, String description) {
        return Alert.builder()
                .patient(patientName)
                .type(type)
                .level(level)
                .description(description)
                .build();
    }

}
