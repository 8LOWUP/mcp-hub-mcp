package com.mcphub.domain.mcp.adviser;

import com.mcphub.domain.mcp.converter.RecommendationConverter;
import com.mcphub.domain.mcp.dto.RecommendationChatMessage;
import com.mcphub.domain.mcp.dto.request.RecommendationRequest;
import com.mcphub.domain.mcp.dto.response.api.RecommendationResponse;
import com.mcphub.domain.mcp.entity.McpVector;
import com.mcphub.domain.mcp.service.mcpRecommendation.McpRecommendationService;
import com.mcphub.domain.mcp.llm.GptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationAdviser {
	private final GptService gptService;
	private final McpRecommendationService mcpRecommendationService;
	private final RecommendationConverter recommendationConverter;

	private final int recommendationNum = 5;

	public RecommendationResponse recommendationChat(RecommendationRequest request) {
		log.info("1111111111111111");
		RecommendationChatMessage recommendationChatMessage = gptService.toRequestAndResponseMessage(
			request.chatMessage());
		log.info("2222222222222");
		float[] embedding = gptService.embedText(recommendationChatMessage.requestText());
		log.info("33333333333333");
		List<McpVector> mcpVectorList = mcpRecommendationService.searchByVector(embedding, recommendationNum);
		log.info("444444444444444");
		return recommendationConverter.toRecommendationResponse(recommendationChatMessage.responseText(),
			mcpVectorList);
	}
}
