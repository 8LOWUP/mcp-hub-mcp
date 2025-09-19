package com.mcphub.domain.mcp.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

	@KafkaListener(topics = "test-topic", groupId = "mcp-group")
	public void consume(String message) {
		System.out.println("📩 Received message: " + message);
	}
}
