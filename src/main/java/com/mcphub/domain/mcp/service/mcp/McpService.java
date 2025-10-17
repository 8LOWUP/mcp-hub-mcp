package com.mcphub.domain.mcp.service.mcp;

import com.mcphub.domain.mcp.dto.request.McpListRequest;
import com.mcphub.domain.mcp.dto.request.MyUploadMcpRequest;
import com.mcphub.domain.mcp.dto.response.api.McpResponse;
import com.mcphub.domain.mcp.dto.response.api.McpSaveResponse;
import com.mcphub.domain.mcp.dto.response.readmodel.McpReadModel;
import com.mcphub.domain.mcp.dto.response.readmodel.TestReadDto;
import com.mcphub.domain.mcp.entity.Mcp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface McpService {

	McpReadModel getMcpDetail(Long McpId, Long userId);

	Page<McpReadModel> getMcpList(Pageable pageable, McpListRequest request);

	McpSaveResponse saveUserMcp(Long userId, Long mcpId);

	Long deleteMcp(Long userId, Long mcpId);

	Page<McpReadModel> getMySavedMcpList(Long userId, Pageable pageable, MyUploadMcpRequest request);
}
