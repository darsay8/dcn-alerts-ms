package dev.rm.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize
public class VitalSignsMessage {
    private Long patientId;
    private Double temperature;
    private String bloodPressure;
    private Integer heartRate;
    private Integer respiratoryRate;
    private Integer oxygenSaturation;
    private Integer glucose;
}