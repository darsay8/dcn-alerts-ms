package dev.rm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlertMessage {
    private String patientName;
    private AlertType type;
    private AlertLevel level;
    private String description;

}
