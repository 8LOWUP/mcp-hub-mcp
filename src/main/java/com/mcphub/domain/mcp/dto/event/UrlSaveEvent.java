package com.mcphub.domain.mcp.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * MCP 배포시 URL 변경됐을 경우 이벤트
 */
@Getter
@AllArgsConstructor
public class UrlSaveEvent {
	private Long mcpId;
	private String url;
}
