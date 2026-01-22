package com.example.pulsar_fitness_app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaNumberService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.kafka.numbers-topic}")
    private String numbersTopic;

    public void sendNumber(int value) {
        kafkaTemplate.send(numbersTopic, Integer.toString(value));
    }
}
