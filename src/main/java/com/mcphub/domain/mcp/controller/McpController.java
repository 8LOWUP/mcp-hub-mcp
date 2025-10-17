package com.mcphub.domain.mcp.controller;

import com.mcphub.domain.mcp.dto.request.MyUploadMcpRequest;
import com.mcphub.domain.mcp.dto.response.api.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import com.mcphub.domain.mcp.adviser.McpAdviser;
import com.mcphub.domain.mcp.dto.request.McpListRequest;
import com.mcphub.global.common.base.BaseResponse;

/**
 * MCP 컨트롤러
 * - MCP 리스트 조회
 * - MCP 상세 조회
 * - MCP 구매/삭제
 * - 내가 등록한 MCP 조회
 */

@RestController
@RequestMapping("/mcps")
@Tag(name = "MCP API", description = "MCP 마켓 관련 API")
@RequiredArgsConstructor
public class McpController {
	private final McpAdviser mcpAdviser;

	/**
	 * 마켓 -> MCP 리스트
	 * @return 조건에 맞는 MCP 리스트
	 */
	@Operation(summary = "MCP 리스트 조회", description = "조건에 맞는 MCP 리스트를 페이징 처리하여 반환합니다.", security = {})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 (파라미터 검증 실패)")
	})
	@GetMapping()
	public BaseResponse<Page<McpResponse>> getMcpList(
		@Parameter(description = "검색/필터 조건") @Valid @ModelAttribute McpListRequest request) {
		Pageable pageable = PageRequest.of(
			request.getPage(),
			Math.min(request.getSize(), 50),
			Sort.by(Sort.Direction.DESC, "id")
		);

		return BaseResponse.onSuccess(
			mcpAdviser.getMcpList(pageable, request)
		);
	}

	/**
	 * MCP 상세 조회
	 * @param mcpId
	 * @return MCP 상세 내용
	 */
	@Operation(summary = "MCP 상세 조회", description = "특정 MCP의 상세 정보를 조회합니다.", security = {})
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "404", description = "해당 MCP가 존재하지 않음")
	})
	@GetMapping("/{mcpId}")
	public BaseResponse<McpDetailResponse> getMcpDetail(@Parameter(description = "MCP ID", required = true)
	                                                    @PathVariable Long mcpId) {
		return BaseResponse.onSuccess(mcpAdviser.getMcpDetail(mcpId));
	}

	/**
	 * MCPHub로 부터 저장
	 * @param mcpId
	 * @return MCP 상세 내용
	 */
	@Operation(summary = "MCP 저장(구매)", description = "마켓에서 MCP를 저장(구매)합니다. 응답으로 해당 MCP가 사용하는 플랫폼의 토큰이 이전에 저장되었는지, 아닌지를 반환합니다. 이전에 저장하지 않았다면 유저는 플랫폼 토큰을 등록해야 해당 MCP를 사용 가능합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "저장 성공"),
		@ApiResponse(responseCode = "402", description = "발행되지 않은 MCP"),
		@ApiResponse(responseCode = "404", description = "MCP가 존재하지 않음"),
		@ApiResponse(responseCode = "409", description = "이미 저장된 MCP")
	})
	@PostMapping("/{mcpId}")
	public BaseResponse<McpSaveResponse> saveUserMcp(@Parameter(description = "MCP ID", required = true)
	                                      @PathVariable Long mcpId) {
		return BaseResponse.onSuccess(mcpAdviser.saveUserMcp(mcpId));
	}

	//TODO : 소프트 삭제? 하드 삭제? 여부 정해야함

	/**
	 * 구매한 MCP 삭제
	 * @param mcpId MCP ID
	 * @return 삭제 결과
	 */
	@Operation(summary = "MCP 삭제", description = "구매한 MCP를 삭제합니다. (소프트/하드 삭제 여부는 추후 확정 필요)")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "삭제 성공"),
		@ApiResponse(responseCode = "404", description = "MCP가 존재하지 않음")
	})
	@DeleteMapping("/{mcpId}")
	public BaseResponse<Long> deleteMcp(@Parameter(description = "MCP ID", required = true)
	                                    @PathVariable Long mcpId) {
		Long deletedId = mcpAdviser.deleteMcp(mcpId);
		return BaseResponse.onSuccess(deletedId);
	}

	/**
	 * 등록한 MCP 리스트 (구매한)
	 * @return 등록한 MCP 리스트
	 */
	@Operation(summary = "내가 저장한 MCP 조회", description = "사용자가 저장한 MCP 리스트를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
	})
	@GetMapping("/me")
	public BaseResponse<Page<MySavedMcpResponse>> getMySavedMcpList(
		@Parameter(description = "페이징 및 검색 조건") @Valid @ModelAttribute MyUploadMcpRequest request) {
		Pageable pageable = PageRequest.of(
			request.getPage(),
			request.getSize(),
			Sort.by(Sort.Direction.DESC, "id")
		);

		return BaseResponse.onSuccess(mcpAdviser.getMySavedMcpList(pageable, request));
	}

	// todo 플랫폼 토큰 엔티티 분리해서 따로 플랫폼 토큰을 관리하는 컨트롤러 서비스 분리 필요, 빠른 구현이 필요해서 일단 MCP 컨트롤러에 포함시킴

	// 플랫폼 토큰 등록
	@Operation(summary = "플랫폼 토큰 등록", description = "특정 플랫폼에 대한 인증 토큰을 등록합니다.")
	@PostMapping("/platform-token/{platformId}")
	public BaseResponse<Long> registerPlatformToken(
			@Parameter(description = "MCP ID", required = true)
			@PathVariable Long platformId,
			@Parameter(description = "등록할 플랫폼 토큰", required = true)
			@RequestParam String platformToken
	) {
		return BaseResponse.onSuccess(mcpAdviser.registerPlatformToken(platformId, platformToken));
	}

	// 내 플랫폼 토큰 조회(내가 등록한 플랫폼 토큰 리스트를 조회합니다.)
	@Operation(summary = "플랫폼 토큰 상태 조회", description = "사용자가 가진 플랫폼별 토큰 등록 여부를 조회합니다.")
	@GetMapping("/platform-token/me")
	public BaseResponse<PlatformTokenStatusListResponse> getMyPlatformTokens() {
		return BaseResponse.onSuccess(mcpAdviser.getMyPlatformTokens());
	}

	// 플랫폼 토큰 삭제
	@Operation(summary = "플랫폼 토큰 삭제", description = "특정 플랫폼에 등록된 토큰을 삭제합니다.")
	@DeleteMapping("/platform-token/{platformId}")
	public BaseResponse<Void> deletePlatformToken(@PathVariable Long platformId) {
		mcpAdviser.deletePlatformToken(platformId);
		return BaseResponse.onSuccess(null);
	}
}
