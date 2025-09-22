package com.mcphub.domain.mcp.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class McpSaveEvent {
	private Long userId;
	private Long mcpId;
}
