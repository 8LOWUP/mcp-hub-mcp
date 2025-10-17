package com.mcphub.domain.mcp.dto.response.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class McpSaveResponse {

    private Long mcpId;

    // 플랫폼 토큰 등록 상태
    private Boolean tokenRegisterStatus;
}
