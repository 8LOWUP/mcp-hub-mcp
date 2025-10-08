package com.mcphub.domain.mcp.dto.response.api;

import lombok.Builder;

@Builder
public record RecommendationResponse(
        String responseText,
        String mcpId
) {
}
