package com.mcphub.domain.mcp.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 유저가 MCP를 저장하거나 삭제 Event
 */
@Getter
@AllArgsConstructor
public class McpSaveEvent {
	private Long userId;
	private Long mcpId;
}
