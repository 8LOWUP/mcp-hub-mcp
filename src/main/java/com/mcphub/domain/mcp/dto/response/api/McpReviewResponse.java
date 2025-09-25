package com.mcphub.domain.mcp.dto.response.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McpReviewResponse {
	private Long reviewId;
	private String userName;
	private double rating;
	private String comment;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private boolean mine;
}
