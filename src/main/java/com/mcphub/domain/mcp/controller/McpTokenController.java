package com.mcphub.domain.mcp.controller;

import com.mcphub.domain.mcp.adviser.McpTokenAdviser;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mcphub.domain.mcp.dto.request.McpTokenRequest;
import com.mcphub.global.common.base.BaseResponse;

/**
 * MCP 토큰 관리 컨트롤러
 * MCP 토큰 저장 및 수정 기능을 제공합니다.
 */

@RestController
@RequestMapping("/mcps/token")
@RequiredArgsConstructor
public class McpTokenController {

	private final McpTokenAdviser mcpTokenAdviser;

	/**
	 * MCP 토큰 저장
	 * @return 토큰 저장 성공
	 */
	@Operation(summary = "MCP 토큰 저장", description = "특정 MCP에 대한 토큰을 저장합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "토큰 저장 성공"),
		@ApiResponse(responseCode = "400", description = "토큰값이 없음"),
		@ApiResponse(responseCode = "404", description = "MCP 또는 UserMcp를 찾을 수 없음")
	})
	@PostMapping("/{mcpId}")
	public BaseResponse<Long> saveMcpToken(@Parameter(description = "MCP ID", required = true) @PathVariable Long mcpId,
	                                       @Parameter(description = "MCP 토큰 요청 데이터") @RequestBody McpTokenRequest request) {
		Long savedTokenMcpId = mcpTokenAdviser.saveMcpToken(mcpId, request);
		return BaseResponse.onSuccess(savedTokenMcpId);
	}

	/**
	 * MCP 토큰 수정
	 * @return 토큰 수정 성공
	 */
	@Operation(summary = "MCP 토큰 수정", description = "저장된 MCP 토큰을 수정합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "토큰 수정 성공"),
		@ApiResponse(responseCode = "400", description = "토큰 값이 없음"),
		@ApiResponse(responseCode = "404", description = "MCP 또는 UserMcp를 찾을 수 없음")
	})
	@PatchMapping("/{mcpId}")
	public BaseResponse<Long> updateMcpToken(
		@Parameter(description = "MCP ID", required = true) @PathVariable Long mcpId,
		@Parameter(description = "수정할 MCP 토큰 요청 데이터") @RequestBody McpTokenRequest request) {
		Long updatedTokenMcpId = mcpTokenAdviser.updateMcpToken(mcpId, request);
		return BaseResponse.onSuccess(updatedTokenMcpId);
	}
}
