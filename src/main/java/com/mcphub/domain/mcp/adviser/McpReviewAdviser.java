package com.mcphub.domain.mcp.adviser;

import com.mcphub.domain.mcp.converter.McpReviewConverter;
import com.mcphub.domain.mcp.dto.request.McpReviewListRequest;
import com.mcphub.domain.mcp.dto.request.McpReviewRequest;
import com.mcphub.domain.mcp.dto.response.api.McpReviewResponse;
import com.mcphub.domain.mcp.dto.response.readmodel.McpReadModel;
import com.mcphub.domain.mcp.dto.response.readmodel.McpReviewReadModel;
import com.mcphub.domain.mcp.service.mcpReview.McpReviewService;
import com.mcphub.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.Page;

@Component
@RequiredArgsConstructor
public class McpReviewAdviser {

	private final SecurityUtils securityUtils;
	private final McpReviewService mcpReviewService;
	private final McpReviewConverter mcpReviewConverter;

	public Page<McpReviewResponse> getReviewList(Long mcpId, McpReviewListRequest request, Pageable pageable) {
		Long userId = securityUtils.getUserId();
		Page<McpReviewReadModel> page = mcpReviewService.getMcpReviewList(pageable, request, mcpId);
		return page.map(m -> mcpReviewConverter.toMcpReviewResponse(m, userId));
	}

	public Long saveReview(Long mcpId, McpReviewRequest request) {
		Long userId = securityUtils.getUserId();
		return mcpReviewService.saveReview(userId, mcpId, request);
	}

	public Long updateReview(Long reviewId, McpReviewRequest request) {
		Long userId = securityUtils.getUserId();
		return mcpReviewService.updateReview(userId, reviewId, request);
	}

	public Long deleteReview(Long reviewId) {
		Long userId = securityUtils.getUserId();
		return mcpReviewService.deleteReview(userId, reviewId);
	}
}
