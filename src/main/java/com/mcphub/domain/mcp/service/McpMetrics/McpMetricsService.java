package com.mcphub.domain.mcp.service.McpMetrics;

import org.springframework.stereotype.Service;

@Service
public interface McpMetricsService {

	void increaseSavedCount(Long mcpId);

	void decreaseSavedCount(Long mcpId);

	void increaseReviewCount(Long mcpId, Double newRating);

	void decreaseReviewCount(Long mcpId, Double deletedRating);

	void updateReview(Long mcpId, Double oldRating, Double newRating);

}
