package dev.rm.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import dev.rm.model.AlertMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertsProducer {

    private final KafkaTemplate<String, AlertMessage> kafkaTemplate;
    private final String topic = "alerts";

    public void sendAlert(AlertMessage message) {
        kafkaTemplate.send(topic, message);
        log.info("Sent alert message to Kafka topic: {}", topic);
    }
}
