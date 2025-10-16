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

	@Transactional
	public void increaseSavedCount(Long mcpId) {
		McpMetrics metrics = getMetrics(mcpId);
		metrics.addSavedCount();
		mcpMetricsRepository.save(metrics);
	}

	@Transactional
	public void decreaseSavedCount(Long mcpId) {
		McpMetrics metrics = getMetrics(mcpId);
		metrics.removeSavedCount();
		mcpMetricsRepository.save(metrics);
	}

	@Transactional
	public void increaseReviewCount(Long mcpId, Double newRating) {
		McpMetrics metrics = getMetrics(mcpId);
		metrics.addReview(newRating);
		mcpMetricsRepository.save(metrics);
	}

	@Transactional
	public void decreaseReviewCount(Long mcpId, Double deletedRating) {
		McpMetrics metrics = getMetrics(mcpId);
		metrics.removeReview(deletedRating);
		mcpMetricsRepository.save(metrics);
	}

	@Transactional
	public void updateReview(Long mcpId, Double oldRating, Double newRating) {
		McpMetrics metrics = getMetrics(mcpId);
		metrics.updateReview(oldRating, newRating);
		mcpMetricsRepository.save(metrics);
	}

	private McpMetrics getMetrics(Long mcpId) {
		return mcpMetricsRepository.findByMcp_Id(mcpId)
		                           .orElseThrow(
			                           () -> new IllegalStateException("McpMetrics not found for MCP id=" + mcpId));
	}
}
