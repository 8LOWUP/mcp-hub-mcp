package com.mcphub.domain.mcp.service.McpMetrics;

import com.mcphub.domain.mcp.entity.McpMetrics;
import com.mcphub.domain.mcp.repository.jsp.McpMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class McpMetricsServiceImpl implements McpMetricsService {
	private final McpMetricsRepository mcpMetricsRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void increaseSavedCount(Long mcpId) {
		McpMetrics metrics = getMetrics(mcpId);
		metrics.addSavedCount();
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void decreaseSavedCount(Long mcpId) {
		McpMetrics metrics = getMetrics(mcpId);
		metrics.removeSavedCount();
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void increaseReviewCount(Long mcpId, Double newRating) {
		McpMetrics metrics = getMetrics(mcpId);
		metrics.addReview(newRating);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void decreaseReviewCount(Long mcpId, Double deletedRating) {
		McpMetrics metrics = getMetrics(mcpId);
		metrics.removeReview(deletedRating);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateReview(Long mcpId, Double oldRating, Double newRating) {
		McpMetrics metrics = getMetrics(mcpId);
		metrics.updateReview(oldRating, newRating);
	}

	private McpMetrics getMetrics(Long mcpId) {
		return mcpMetricsRepository.findByMcp_Id(mcpId)
		                           .orElseThrow(
			                           () -> new IllegalStateException("McpMetrics not found for MCP id=" + mcpId));
	}
}
