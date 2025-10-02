package com.mcphub.domain.mcp.controller;

import com.mcphub.domain.mcp.dto.request.MyUploadMcpRequest;
import com.mcphub.domain.mcp.dto.response.api.McpDetailResponse;
import com.mcphub.domain.mcp.dto.response.api.MySavedMcpResponse;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mcphub.domain.mcp.adviser.McpAdviser;
import com.mcphub.domain.mcp.dto.request.McpListRequest;
import com.mcphub.global.common.base.BaseResponse;
import com.mcphub.domain.mcp.dto.response.api.McpResponse;

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
	@Operation(summary = "MCP 저장(구매)", description = "마켓에서 MCP를 저장(구매)합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "저장 성공"),
		@ApiResponse(responseCode = "402", description = "발행되지 않은 MCP"),
		@ApiResponse(responseCode = "404", description = "MCP가 존재하지 않음"),
		@ApiResponse(responseCode = "409", description = "이미 저장된 MCP")
	})
	@PostMapping("/{mcpId}")
	public BaseResponse<Long> saveUserMcp(@Parameter(description = "MCP ID", required = true)
	                                      @PathVariable Long mcpId) {
		Long saveId = mcpAdviser.saveUserMcp(mcpId);
		return BaseResponse.onSuccess(saveId);
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
}
