package com.mcphub.domain.mcp.dto.response.readmodel;

import com.mcphub.domain.mcp.dto.response.api.McpToolResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McpReadModel {
	private Long id;
	private String name;
	private String version;
	private String description;
	private String imageUrl;
	private String sourceUrl;
	private Boolean isKeyRequired;
	private String developerName;
	private Long categoryId;
	private String categoryName;
	private Long platformId;
	private String platformName;
	private Long licenseId;
	private String licenseName;
    private String requestUrl;

	private Double averageRating;     // null 가능
	private Long savedUserCount;
	private boolean isPublished;
	private LocalDateTime publishedAt;
	private LocalDateTime lastPublishedAt;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	//TODO : McpReadModel , detailReadModel 구분해야하는지 확인 필요
	private List<McpToolResponse> tools;
}
