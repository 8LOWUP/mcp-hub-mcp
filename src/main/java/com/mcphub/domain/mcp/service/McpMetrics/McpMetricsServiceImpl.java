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
		metrics.setSavedUserCount(metrics.getSavedUserCount() + 1);
		mcpMetricsRepository.save(metrics);
	}

	@Transactional
	public void decreaseSavedCount(Long mcpId) {
		McpMetrics metrics = getMetrics(mcpId);
		if (metrics.getSavedUserCount() > 0) {
			metrics.setSavedUserCount(metrics.getSavedUserCount() - 1);
			mcpMetricsRepository.save(metrics);
		}
	}

	@Transactional
	public void increaseReviewCount(Long mcpId, Double newRating) {
		McpMetrics metrics = getMetrics(mcpId);

		int newCount = metrics.getReviewCount() + 1;
		double newSum = metrics.getReviewScoreSum() + newRating;
		double newAvg = newSum / newCount;

		metrics.setReviewCount(newCount);
		metrics.setReviewScoreSum(newSum);
		metrics.setAvgRating(newAvg);

		mcpMetricsRepository.save(metrics);
	}

	@Transactional
	public void decreaseReviewCount(Long mcpId, Double deletedRating) {
		McpMetrics metrics = getMetrics(mcpId);

		if (metrics.getReviewCount() > 0) {
			int newCount = metrics.getReviewCount() - 1;
			double newSum = metrics.getReviewScoreSum() - deletedRating;
			double newAvg = newCount == 0 ? 0.0 : newSum / newCount;

			metrics.setReviewCount(newCount);
			metrics.setReviewScoreSum(newSum);
			metrics.setAvgRating(newAvg);

			mcpMetricsRepository.save(metrics);
		}
	}

	@Transactional
	public void updateReview(Long mcpId, Double oldRating, Double newRating) {
		McpMetrics metrics = getMetrics(mcpId);

		double newSum = metrics.getReviewScoreSum() - oldRating + newRating;
		double newAvg = metrics.getReviewCount() == 0 ? 0.0 : newSum / metrics.getReviewCount();

		metrics.setReviewScoreSum(newSum);
		metrics.setAvgRating(newAvg);

		mcpMetricsRepository.save(metrics);
	}

	private McpMetrics getMetrics(Long mcpId) {
		return mcpMetricsRepository.findByMcp_Id(mcpId)
		                           .orElseThrow(
			                           () -> new IllegalStateException("McpMetrics not found for MCP id=" + mcpId));
	}
}
