package com.mcphub.domain.mcp.dto.response.readmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PlatformTokenReadModel {
    private Long platformId;
    private boolean tokenRegistered;
}

