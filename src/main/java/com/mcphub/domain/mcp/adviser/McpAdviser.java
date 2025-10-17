package com.mcphub.domain.mcp.adviser;

import com.mcphub.domain.mcp.dto.request.McpListRequest;
import com.mcphub.domain.mcp.dto.request.MyUploadMcpRequest;
import com.mcphub.domain.mcp.dto.response.api.*;
import com.mcphub.domain.mcp.dto.response.readmodel.McpReadModel;
import com.mcphub.domain.mcp.dto.response.readmodel.PlatformTokenReadModel;
import com.mcphub.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import com.mcphub.domain.mcp.converter.McpConverter;
import com.mcphub.domain.mcp.service.mcp.McpService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class McpAdviser {
	private final McpService mcpService;
	private final McpConverter mcpConverter;
	private final SecurityUtils securityUtils;

	public Page<McpResponse> getMcpList(Pageable pageable, McpListRequest req) {
		Page<McpReadModel> page = mcpService.getMcpList(pageable, req);
		return page.map(mcpConverter::toMcpResponse);
	}

	public McpDetailResponse getMcpDetail(Long id) {
		Long userId = securityUtils.getUserId();
		return mcpConverter.toMcpDetailResponse(mcpService.getMcpDetail(id, userId));
	}

	// MCP 저장(구매)
	public McpSaveResponse saveUserMcp(Long mcpId) {
		Long userId = securityUtils.getUserId();
		return mcpService.saveUserMcp(userId, mcpId);
	}

	public Long deleteMcp(Long mcpId) {
		Long userId = securityUtils.getUserId();
		return mcpService.deleteMcp(userId, mcpId);
	}

	public Page<MySavedMcpResponse> getMySavedMcpList(Pageable pageable, MyUploadMcpRequest req) {
		Long userId = securityUtils.getUserId();
		Page<McpReadModel> page = mcpService.getMySavedMcpList(userId, pageable, req);
		return page.map(mcpConverter::toMyUploadMcpResponse);
	}

	// todo 플랫폼 토큰 관련 로직 분리 필요
	//플랫폼 토큰 관련
	public Long registerPlatformToken(Long platformId, String token) {
		Long userId = securityUtils.getUserId();
		return mcpService.registerPlatformToken(userId, platformId, token);
	}

	public Long updatePlatformToken(Long platformId, String token) {
		Long userId = securityUtils.getUserId();
		return mcpService.updatePlatformToken(userId, platformId, token);
	}

	public void deletePlatformToken(Long platformId) {
		Long userId = securityUtils.getUserId();
		mcpService.deletePlatformToken(userId, platformId);
	}

	public PlatformTokenStatusListResponse getMyPlatformTokens() {
		Long userId = securityUtils.getUserId();
		List<PlatformTokenReadModel> readModels = mcpService.getMyPlatformTokens(userId);

		return mcpConverter.toPlatformTokenStatusResponseList(readModels);
	}

}
