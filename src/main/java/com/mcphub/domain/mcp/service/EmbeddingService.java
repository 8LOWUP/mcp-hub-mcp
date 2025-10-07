package com.mcphub.domain.mcp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * 텍스트를 벡터로 변환하는 임베딩 서비스
 * OpenAI API를 사용하는 예제
 */
@Slf4j
@Service
public class EmbeddingService {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${openai.api.url:https://api.openai.com/v1/embeddings}")
    private String openaiApiUrl;

    @Value("${openai.model:text-embedding-3-small}")
    private String embeddingModel;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public EmbeddingService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * OpenAI API를 사용한 임베딩
     */
    public float[] embedText(String text) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openaiApiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("input", text);
            requestBody.put("model", embeddingModel);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            String response = restTemplate.postForObject(openaiApiUrl, request, String.class);

            return parseEmbeddingResponse(response);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return new float[0];
    }

    /**
     * OpenAI 응답 파싱
     */
    private float[] parseEmbeddingResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode embeddingNode = root.path("data").get(0).path("embedding");

            int size = embeddingNode.size();
            float[] embedding = new float[size];

            for (int i = 0; i < size; i++) {
                embedding[i] = (float) embeddingNode.get(i).asDouble();
            }

            return embedding;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return new float[0];
    }

}
