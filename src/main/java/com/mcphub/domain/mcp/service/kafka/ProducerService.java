package com.mcphub.domain.mcp.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcphub.domain.mcp.dto.event.McpSaveEvent;
import com.mcphub.domain.mcp.dto.event.UrlSaveEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProducerService {

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;

	public void sendMessage(String topic, String message) {
		kafkaTemplate.send(topic, message);
		log.info("Message sent to topic: " + topic + ", message: " + message);
	}

	public void sendMessage(String topic, String key, Object value) {
		try {
			String payload = objectMapper.writeValueAsString(value);
			kafkaTemplate.send(topic, key, payload);
			log.info("Message sent to topic: " + topic + ", key: " + key + ", payload: " + payload);
			System.out.println("Sent message with key " + key + ": " + payload);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Kafka message serialization failed", e);
		}
	}

	//유저가 MCP 저장
	public void sendMcpSavedEvent(Long userId, Long mcpId) {
		sendMessage("user-saved-mcp", userId.toString(), new McpSaveEvent(userId, mcpId));
	}

	//TODO : MCP 삭제 시 메서드명 변경 필요 (MCP 삭제가 어떤 상황인지를 의미하게 ...)
	//유저가 저장된 MCP 삭제
	public void sendMcpDeletedEvent(Long userId, Long mcpId) {
		sendMessage("user-deleted-mcp", userId.toString(), new McpSaveEvent(userId, mcpId));
	}

	//MCP URL 저장 (배포 시)
	public void sendUrlSavedEvent(Long mcpId, String url) {
		sendMessage("mcp-saved-url", mcpId.toString(), new UrlSaveEvent(mcpId, url));
	}

	//MCP URL 삭제 (MCP가 삭제되거나, MCP가 미배포 상태로 전환 시)
	public void sendUrlDeletedEvent(Long mcpId) {
		sendMessage("mcp-deleted-url", mcpId.toString());
	}
}
