package com.mcphub.domain.mcp.controller;

import com.mcphub.domain.mcp.adviser.McpDashboardAdviser;
import com.mcphub.domain.mcp.dto.request.McpListRequest;
import com.mcphub.domain.mcp.dto.response.api.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import com.mcphub.domain.mcp.dto.request.McpDraftRequest;
import com.mcphub.domain.mcp.dto.request.McpUploadDataRequest;
import com.mcphub.domain.mcp.dto.request.McpUrlRequest;
import com.mcphub.global.common.base.BaseResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/mcps/dashboard")
@RequiredArgsConstructor
public class McpDashboardController {

	private final McpDashboardAdviser mcpDashboardAdviser;

	@Operation(summary = "내 업로드 MCP 리스트 조회", description = "사용자가 업로드한 MCP 리스트를 페이징 처리하여 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
	})
	@GetMapping()
	public BaseResponse<Page<McpResponse>> getMyUploadMcpList(
		@Parameter(description = "검색/필터 조건") @Valid @ModelAttribute McpListRequest request) {
		Pageable pageable = PageRequest.of(
			request.getPage(),
			Math.min(request.getSize(), 50),
			Sort.by(Sort.Direction.DESC, "id")
		);

		return BaseResponse.onSuccess(
			mcpDashboardAdviser.getMyUploadMcpList(pageable, request)
		);
	}

	/**
	 * 업로드한 Mcp 상세
	 * @return Mcp 상세 (provider)
	 */
	@Operation(summary = "업로드 MCP 상세 조회", description = "특정 MCP의 업로드 상세 정보를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "403", description = "본인 소유가 아닌 MCP 접근"),
		@ApiResponse(responseCode = "404", description = "MCP를 찾을 수 없음")
	})
	@GetMapping("/{mcpId}")
	public BaseResponse<MyUploadMcpDetailResponse> getUploadMcpDetail(
		@Parameter(description = "MCP ID", required = true) @PathVariable Long mcpId) {
		return BaseResponse.onSuccess(mcpDashboardAdviser.getUploadMcpDetail(mcpId));
	}

	/**
	 * 플랫폼 리스트 반환
	 * @return 플랫폼
	 */
	@Operation(summary = "MCP 플랫폼 리스트 (구현 예정)", description = "플랫폼 리스트를 반환합니다")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "플랫폼 리턴 성공")
	})
	@PatchMapping("/platform")
	public BaseResponse<Long> getPlatform(
		@Parameter(description = "MCP ICON", required = false) @RequestParam("file") MultipartFile file,
		@Parameter(description = "배포 요청 데이터") @RequestPart("meta") McpUploadDataRequest request) {

		return BaseResponse.onSuccess(mcpDashboardAdviser.uploadMcpMetaData(request, file));
	}

	/**
	 * MCP 메타 데이터 등록
	 * @return Mcp 메타 데이터 등록
	 */
	@Operation(summary = "MCP 메타데이터 등록", description = "특정 MCP에 메타데이터를 등록합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "메타데이터 등록 성공"),
		@ApiResponse(responseCode = "400", description = "올바르지않은 요청"),
		@ApiResponse(responseCode = "401", description = "로그인 필요 서비스"),
		@ApiResponse(responseCode = "403", description = "본인 소유가 아닌 MCP 접근"),
		@ApiResponse(responseCode = "404", description = "MCP/카테고리/플랫폼/라이선스가 존재하지 않음")
	})
	@PatchMapping("/meta")
	public BaseResponse<Long> uploadMcpMetaData(
		@Parameter(description = "MCP ICON", required = false) @RequestParam("file") MultipartFile file,
		@Parameter(description = "배포 요청 데이터") @RequestPart("meta") McpUploadDataRequest request) {

		return BaseResponse.onSuccess(mcpDashboardAdviser.uploadMcpMetaData(request, file));
	}

	/**
	 * MCP 최종 배포
	 * @return Mcp 최종 배포
	 */
	@Operation(summary = "MCP 배포", description = "특정 MCP를 배포/미배포 처리합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "배포 상태 변경 성공"),
		@ApiResponse(responseCode = "400", description = "올바르지않은 요청"),
		@ApiResponse(responseCode = "401", description = "로그인 필요 서비스"),
		@ApiResponse(responseCode = "403", description = "본인 소유가 아닌 MCP 접근"),
		@ApiResponse(responseCode = "404", description = "MCP를 찾을 수 없음")
	})
	@PatchMapping("/publish")
	public BaseResponse<Long> publishMcp(
		@Parameter(description = "MCP ICON", required = false) @RequestParam("file") MultipartFile file,
		@Parameter(description = "배포 요청 데이터") @RequestPart("meta") McpUploadDataRequest request) {
		return BaseResponse.onSuccess(mcpDashboardAdviser.publishMcp(request, file));
	}

	/**
	 * MCP 삭제 (provider)
	 * @param mcpId
	 * @return MCP 삭제
	 */
	@Operation(summary = "MCP 삭제", description = "업로드한 MCP를 삭제합니다. (soft delete)")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "삭제 성공"),
		@ApiResponse(responseCode = "403", description = "본인 소유가 아닌 MCP 접근"),
		@ApiResponse(responseCode = "404", description = "MCP를 찾을 수 없음")
	})
	@DeleteMapping("{mcpId}")
	public BaseResponse<Long> deleteMcp(@Parameter(description = "MCP ID", required = true) @PathVariable Long mcpId) {
		Long deletedMcpId = mcpDashboardAdviser.deleteMcp(mcpId);
		return BaseResponse.onSuccess(deletedMcpId);
	}


    //테스트용 임시 컨트롤러
    @GetMapping("create/{mcpId}")
    public BaseResponse<String> createMcp(@PathVariable Long mcpId, @RequestParam String name, @RequestParam String description) {
        return BaseResponse.onSuccess(mcpDashboardAdviser.createMcp(mcpId, name, description));
    }

    @GetMapping("/vector")
    public BaseResponse<RecommendationResponse> getMcpByText(@RequestParam String requestText) {
        return BaseResponse.onSuccess(mcpDashboardAdviser.getMcpByText(requestText));
    }
}
