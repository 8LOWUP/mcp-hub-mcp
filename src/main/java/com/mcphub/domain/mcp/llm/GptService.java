package com.mcphub.domain.mcp.llm;

import com.mcphub.domain.mcp.dto.RecommendationChatMessage;
import com.mcphub.global.common.exception.RestApiException;
import com.mcphub.global.common.exception.code.status.GlobalErrorStatus;
import lombok.RequiredArgsConstructor;
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

/**
 * 텍스트를 벡터로 변환하는 임베딩 서비스
 * OpenAI API를 사용하는 예제
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GptService {

	@Value("${openai.api.key}")
	private String openaiApiKey;

	@Value("${openai.api.embeddingUrl:https://api.openai.com/v1/embeddings}")
	private String openaiEmbeddingApiUrl;

	@Value("${openai.api.chatUrl:https://api.openai.com/v1/chat/completions}")
	private String openaiChatApiUrl;

	@Value("${openai.embeddingModel:text-embedding-3-small}")
	private String embeddingModel;

	@Value("${openai.chatModel:gpt-5}")
	private String chatModel;

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

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

			String response = restTemplate.postForObject(openaiEmbeddingApiUrl, request, String.class);

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
			log.info("embedding start : " + response);
			log.info("response : " + response);
			JsonNode root = objectMapper.readTree(response);
			JsonNode embeddingNode = root.path("data").get(0).path("embedding");

			int size = embeddingNode.size();
			float[] embedding = new float[size];

			for (int i = 0; i < size; i++) {
				embedding[i] = (float)embeddingNode.get(i).asDouble();
			}

			return embedding;

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return new float[0];
	}

	public RecommendationChatMessage toRequestAndResponseMessage(String text) {
		try {

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(openaiApiKey);

			Map<String, Object> requestBody = new HashMap<>();
			requestBody.put("model", chatModel);
			requestBody.put("messages", new Object[] {new HashMap<String, String>() {{
				put("role", "system");
				put("content", "너는 Mcp를 추천하는 에이전트야");
			}},
				new HashMap<String, String>() {{
					put("role", "user");
					put("content", generatePrompt(text));
				}}
			});

			HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

			String response = restTemplate.postForObject(openaiChatApiUrl, request, String.class);
			JsonNode root = objectMapper.readTree(response);
			String content = root.path("choices").get(0).path("message").path("content").asText();
			JsonNode contentJson = objectMapper.readTree(content);
			String requestText = contentJson.get("requestText").asText();
			String responseText = contentJson.get("responseText").asText();

			return new RecommendationChatMessage(requestText, responseText);

		} catch (Exception e) {
			throw new RestApiException(GlobalErrorStatus._INTERNAL_SERVER_ERROR);
		}
	}

	private String generatePrompt(String text) {
		StringBuilder prompt = new StringBuilder();
		prompt.append("""
			유저가 입력한 내용 중에서 핵심 정보를 추출할거야. 그리고 적절한 답변도 함께 생성해야해.
			핵심 정보를 바탕으로 vector DB에서 데이터를 가져오는 과정은 서버 내부에서 진행하니까 신경쓰지 않아도 돼.
			response 형식은 다음 형식을 반드시 지켜야 해.
			{"requestText": "string", "responseText": "string"}
			
			다음은 예시야.
			유저: 노션으로 문서를 수정할 수 있는 MCP를 찾아줘.
			답변: {"requestText": "노션 문서 수정", "responseText": "다음은 노션으로 문서를 수정하기 위한 적절한 MCP 목록입니다."}
			
			responseText에서는 실제 mcp를 추천하는 것이 아니라 이런 목록이 있다라고만 출력해줘.
			""");

		prompt.append("""
			다음부터는 실제 유저의 요청이야. 형식에 맞춰 답변을 보내줘.
			유저: %s
			""".formatted(text));

		return prompt.toString();
	}

}
