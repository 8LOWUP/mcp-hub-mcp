package com.mcphub.domain.mcp.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcphub.domain.mcp.llm.GptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventHandler {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final GptService gptService;

    @KafkaListener(topics = "${spring.kafka.source-database-topic}", groupId = "mcp-backend-group")
    public void handleChange(String message) {
        log.info("called kafka handlechange");
        try {
            JsonNode root = objectMapper.readTree(message);
            JsonNode after = root.path("payload").path("after");
            if (after.isMissingNode()) {
                return; // 삭제된 경우 무시
            }

            // DB 컬럼 매핑
            Long mcpId = after.path("id").asLong();               // PK
            String name = after.path("name").asText("");          // 이름
            String description = after.path("description").asText(""); // 설명 등 텍스트

            // GPTService 통해 임베딩 생성
            float[] embedding = gptService.embedText(description);

            // McpVector 구조에 맞는 JSON 생성
            Map<String, Object> out = new HashMap<>();
            out.put("mcpId", mcpId);
            out.put("name", name);
            out.put("description", description);
            out.put("embedding", embedding);

            String outJson = objectMapper.writeValueAsString(out);

            // Kafka 전송
            kafkaTemplate.send("backend-to-elasticsearch", String.valueOf(mcpId), outJson);

            log.info("Sent vectorized MCP to backend-to-elasticsearch: {}", mcpId);

        } catch (Exception e) {
            log.error("Error handling change event", e);
        }
    }
}