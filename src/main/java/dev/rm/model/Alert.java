package dev.rm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "alerts")
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long alertId;

    @Column(name = "patient_id")
    private Long patientId;

    @Column(name = "vital_sign_id")
    private Long vitalSignId;

    @Column(name = "alert_type")
    private AlertType type;

    @Column(name = "alert_level")
    private AlertLevel level;

    @Column(name = "description")
    private String description;

    @Column(name = "timestamp")
    private Long timestamp;

}