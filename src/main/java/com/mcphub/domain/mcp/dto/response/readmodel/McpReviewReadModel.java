package com.mcphub.domain.mcp.dto.response.readmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McpReviewReadModel {
	private Long id;
	private Long userSeq;
	private String userName;
	private double rating;
	private String comment;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
