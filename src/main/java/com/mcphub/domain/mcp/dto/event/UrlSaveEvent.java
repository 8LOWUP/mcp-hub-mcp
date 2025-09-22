package com.mcphub.domain.mcp.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UrlSaveEvent {
	private Long mcpId;
	private String url;
}
