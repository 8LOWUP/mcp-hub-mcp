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
        RecommendationChatMessage recommendationChatMessage = gptService.toRequestAndResponseMessage(request.chatMessage());

        float[] embedding = gptService.embedText(recommendationChatMessage.requestText());
        List<McpVector> mcpVectorList = mcpRecommendationService.searchByVector(embedding, recommendationNum);
        return recommendationConverter.toRecommendationResponse(recommendationChatMessage.responseText(), mcpVectorList);
    }
}
