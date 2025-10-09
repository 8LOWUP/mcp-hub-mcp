package com.mcphub.domain.mcp.dto.response.api;

import lombok.Builder;

import java.util.List;

@Builder
public record RecommendationResponse(
        String responseText,
        List<McpInfo> mcpList
) {
    public record McpInfo(
            String mcpId,
            String mcpName
    ) {}
}
