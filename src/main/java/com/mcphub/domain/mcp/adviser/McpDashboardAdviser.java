package com.mcphub.domain.mcp.adviser;

import com.mcphub.domain.mcp.converter.McpDashboardConverter;
import com.mcphub.domain.mcp.dto.request.McpDraftRequest;
import com.mcphub.domain.mcp.dto.request.McpListRequest;
import com.mcphub.domain.mcp.dto.request.McpUploadDataRequest;
import com.mcphub.domain.mcp.dto.request.McpUrlRequest;
import com.mcphub.domain.mcp.dto.response.api.McpResponse;
import com.mcphub.domain.mcp.dto.response.api.McpUrlResponse;
import com.mcphub.domain.mcp.dto.response.api.MyUploadMcpDetailResponse;
import com.mcphub.domain.mcp.dto.response.readmodel.McpReadModel;
import com.mcphub.domain.mcp.service.mcpDashboard.McpDashboardService;
import com.mcphub.global.common.exception.RestApiException;
import com.mcphub.global.common.exception.code.status.GlobalErrorStatus;
import com.mcphub.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class McpDashboardAdviser {

	private final McpDashboardService mcpDashboardService;
	private final McpDashboardConverter mcpDashboardConverter;
	private final SecurityUtils securityUtils;

	public Page<McpResponse> getMyUploadMcpList(Pageable pageable, McpListRequest req) {
		Long userId = securityUtils.getUserId();
		Page<McpReadModel> page = mcpDashboardService.getMyUploadMcpList(pageable, req, userId);
		return page.map(mcpDashboardConverter::toMcpResponse);
	}

	public MyUploadMcpDetailResponse getUploadMcpDetail(Long mcpId) {
		Long userId = securityUtils.getUserId();
		return mcpDashboardConverter.toMyUploadMcpDetailResponse(
			mcpDashboardService.getUploadMcpDetail(userId, mcpId)
		);
	}

	public Long createMcpDraft(McpDraftRequest request) {
		Long userId = securityUtils.getUserId();
		return mcpDashboardService.createMcpDraft(userId, request);
	}

	public Long uploadMcpUrl(Long mcpId, McpUrlRequest request) {
		Long userId = securityUtils.getUserId();
		return mcpDashboardService.uploadMcpUrl(userId, mcpId, request);
	}

	public Long uploadMcpMetaData(McpUploadDataRequest request, MultipartFile file) {
		Long userId = securityUtils.getUserId();
		if (userId == null) {
			log.info("============= USER NAME IS NULL");
			throw new RestApiException(GlobalErrorStatus._UNAUTHORIZED);
		}
		return mcpDashboardService.uploadMcpMetaData(userId, request, file);
	}

	public Long publishMcp(McpUploadDataRequest request, MultipartFile file) {
		Long userId = securityUtils.getUserId();
		if (userId == null) {
			throw new RestApiException(GlobalErrorStatus._UNAUTHORIZED);
		}
		return mcpDashboardService.publishMcp(userId, request, file);
	}

	public Long deleteMcp(Long mcpId) {
		Long userId = securityUtils.getUserId();
		return mcpDashboardService.deleteMcp(userId, mcpId);
	}

	public List<String> getPlatform() {
		return mcpDashboardService.getPlatform();
	}
}
