package com.mcphub.domain.mcp.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;

@Service
@RequiredArgsConstructor
public class ProducerService {

	private final KafkaTemplate<String, String> kafkaTemplate;

	public void sendMessage(String topic, String message) {
		kafkaTemplate.send(topic, message);
		System.out.println("✅ Sent message: " + message);
	}
}
