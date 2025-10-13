package com.mcphub.domain.mcp.dto.request;

import jakarta.validation.constraints.NotNull;
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
public class McpPublishDataRequest {

	private Long mcpId;
	@NotNull(message = "MCP 이름을 입력해주세요")
	private String name;
	//private String version;

	@NotNull(message = "MCP 설명을 적어주세요")
	private String description;

	@NotNull(message = "카테고리 값이 필요")
	private Long categoryId;

	@NotNull(message = "라이선스 값이 필요")
	private Long licenseId;

	@NotNull(message = "참고 자료 URL 필요")
	private String sourceUrl;
	//private String version;
	//플랫폼 이름으로 서치
	@NotNull(message = "플랫폼 정보 필요")
	private String platformName;

	@NotNull(message = "MCP 요청 URL이 필요")
	private String requestUrl;

	@NotNull(message = "개발자 이름 필요")
	private String developerName;

	@NotNull(message = "MCP 접근 시 키값이 필요한지에 대한 여부 필요")
	private Boolean isKeyRequired;

	@NotNull(message = "MCP TOOL에 대한 설명 필요")
	private List<McpToolRequest> tools;
}

