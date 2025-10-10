package com.mcphub.domain.mcp.service.mcpDashboard;

import com.mcphub.domain.mcp.dto.request.McpDraftRequest;
import com.mcphub.domain.mcp.dto.request.McpListRequest;
import com.mcphub.domain.mcp.dto.request.McpUploadDataRequest;
import com.mcphub.domain.mcp.dto.request.McpUrlRequest;
import com.mcphub.domain.mcp.dto.response.readmodel.McpReadModel;
import com.mcphub.domain.mcp.dto.response.readmodel.MyUploadMcpDetailReadModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface McpDashboardService {

	Page<McpReadModel> getMyUploadMcpList(Pageable pageable, McpListRequest request, Long userId);

	MyUploadMcpDetailReadModel getUploadMcpDetail(Long userId, Long mcpId);

	Long createMcpDraft(Long userId, McpDraftRequest request);

	Long uploadMcpUrl(Long userId, Long mcpId, McpUrlRequest request);

	Long uploadMcpMetaData(Long userId, McpUploadDataRequest request, MultipartFile file);

	Long publishMcp(Long userId, McpUploadDataRequest request, MultipartFile file);

	Long deleteMcp(Long userId, Long mcpId);

	List<String> getPlatform();
}
