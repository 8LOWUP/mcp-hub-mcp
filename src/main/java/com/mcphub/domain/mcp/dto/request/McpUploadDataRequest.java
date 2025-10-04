package com.mcphub.domain.mcp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McpUploadDataRequest {
	private Long mcpId;
	private String name;
	//private String version;
	private String description;
	private Long categoryId;
	private Long licenseId;
	private String sourceUrl;
	private String imageUrl;
	//private String version;
	//플랫폼 이름으로 서치
	private String platformName;
	private String requestUrl;
	private String developerName;
	private Boolean isKeyRequired;
	private List<McpToolRequest> tools;
}
