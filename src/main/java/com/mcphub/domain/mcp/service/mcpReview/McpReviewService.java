package com.mcphub.domain.mcp.service.mcpReview;

import com.mcphub.domain.mcp.dto.request.McpReviewListRequest;
import com.mcphub.domain.mcp.dto.request.McpReviewRequest;
import com.mcphub.domain.mcp.dto.response.readmodel.McpReviewReadModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface McpReviewService {

	Page<McpReviewReadModel> getMcpReviewList(Pageable pageable, McpReviewListRequest request, Long mcpId);

	Long saveReview(Long userId, Long mcpId, McpReviewRequest request);

	Long updateReview(Long userId, Long reviewId, McpReviewRequest request);
	
	Long deleteReview(Long userId, Long reviewId);
}
