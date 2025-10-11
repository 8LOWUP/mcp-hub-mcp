package com.mcphub.domain.mcp.converter;

import com.mcphub.domain.mcp.dto.response.api.RecommendationResponse;
import com.mcphub.domain.mcp.entity.McpVector;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RecommendationConverter {
    public RecommendationResponse toRecommendationResponse(String responseText, List<McpVector> mcpVectorList) {
        List<RecommendationResponse.McpInfo> mcpInfoList = mcpVectorList.stream()
                .map(mcp -> new RecommendationResponse.McpInfo(
                        mcp.getMcpId().toString(),
                        mcp.getName()
                ))
                .toList();

        return RecommendationResponse.builder()
                .responseText(responseText)
                .mcpList(mcpInfoList)
                .build();
    }
}
