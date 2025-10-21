package com.mcphub.domain.mcp.converter;

import com.mcphub.domain.mcp.dto.response.api.RecommendationResponse;
import com.mcphub.domain.mcp.entity.McpVector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class RecommendationConverter {
	public RecommendationResponse toRecommendationResponse(String responseText, List<McpVector> mcpVectorList) {
		log.info("reponseTest : " + responseText);
		List<RecommendationResponse.McpInfo> mcpInfoList = mcpVectorList.stream()
		                                                                .map(mcp -> new RecommendationResponse.McpInfo(
			                                                                mcp.getMcpId().toString(),
			                                                                mcp.getName()
		                                                                ))
		                                                                .toList();
		log.info("mcpInfoList : " + mcpInfoList.size());
		return RecommendationResponse.builder()
		                             .responseText(responseText)
		                             .mcpList(mcpInfoList)
		                             .build();
	}
}
