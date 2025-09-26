package com.mcphub.domain.mcp.converter;

import com.mcphub.domain.mcp.dto.response.api.McpReviewResponse;
import com.mcphub.domain.mcp.dto.response.readmodel.McpReviewReadModel;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class McpReviewConverter {

	public McpReviewResponse toMcpReviewResponse(McpReviewReadModel m, Long userId) {
		return McpReviewResponse.builder()
		                        .reviewId(m.getId())
		                        .userName(m.getUserName())
		                        .rating(m.getRating())
		                        .comment(m.getComment())
		                        .createdAt(m.getCreatedAt())
		                        .updatedAt(m.getUpdatedAt())
		                        .mine(Objects.equals(userId, m.getUserSeq()))
		                        .build();

	}
}
