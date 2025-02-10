package dev.rm.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.stereotype.Service;

import dev.rm.model.PatientMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientStatusProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendPatientStatus(PatientMessage message) {
        log.info("Sending patient status update for Patient ID: {}", message.getPatientId());
        rabbitTemplate.convertAndSend("patientStatusQueue", message);
    }
}
