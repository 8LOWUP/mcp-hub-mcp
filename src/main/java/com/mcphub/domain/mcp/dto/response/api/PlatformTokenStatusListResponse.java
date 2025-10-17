package com.mcphub.domain.mcp.dto.response.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlatformTokenStatusListResponse {

    private List<PlatformTokenStatusResponse> tokens;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PlatformTokenStatusResponse {
        private Long platformId;
        private boolean tokenRegistered;
    }
}

