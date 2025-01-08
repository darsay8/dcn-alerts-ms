package dev.rm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VitalSigns {
    private Long vitalSignId;
    private Long patientId;
    private String temperature;
    private String bloodPressure;
    private String heartRate;
    private String respiratoryRate;
    private String oxygenSaturation;
    private String glucose;
    private String notes;
    private String timestamp;

}
