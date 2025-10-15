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
public class MySavedMcpResponse {
	private Long id;
	private String name;
	private String version;
	private String description;
	private String imageUrl;
	private Long categoryId;
	private Long licenseId;
	private Long platformId;
	private String platformName;
	private LocalDateTime createdAt;
}
