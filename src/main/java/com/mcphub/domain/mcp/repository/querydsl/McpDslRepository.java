package com.mcphub.domain.mcp.repository.querydsl;

import com.mcphub.domain.mcp.dto.request.McpListRequest;
import com.mcphub.domain.mcp.dto.request.MyUploadMcpRequest;
import com.mcphub.domain.mcp.dto.response.api.McpToolResponse;
import com.mcphub.domain.mcp.dto.response.readmodel.McpReadModel;
import com.mcphub.domain.mcp.dto.response.readmodel.MyUploadMcpDetailReadModel;
import com.mcphub.domain.mcp.entity.Mcp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface McpDslRepository {

	// ==== MCP MARKET ====
	Page<McpReadModel> searchMcps(McpListRequest req, Pageable pageable);

	McpReadModel getMcpDetail(Long id);

	Page<McpReadModel> getMySavedMcpList(Long userId, Pageable pageable, MyUploadMcpRequest req);

	// ==== MCP DASHBOARD ====
	Page<McpReadModel> searchMyUploadMcps(McpListRequest req, Pageable pageable, Long userId);

	MyUploadMcpDetailReadModel getMyUploadMcpDetail(Long mcpId);

	List<McpToolResponse> getMcpTools(Long mcpId);
}
