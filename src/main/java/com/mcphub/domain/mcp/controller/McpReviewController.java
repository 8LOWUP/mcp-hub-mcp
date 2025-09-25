package com.mcphub.domain.mcp.controller;

import com.mcphub.domain.mcp.adviser.McpReviewAdviser;
import com.mcphub.domain.mcp.dto.request.McpReviewListRequest;
import com.mcphub.domain.mcp.dto.request.McpReviewRequest;
import com.mcphub.domain.mcp.dto.response.api.McpReviewResponse;
import com.mcphub.global.common.base.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mcps/review")
@RequiredArgsConstructor
public class McpReviewController {

	private final McpReviewAdviser mcpReviewAdviser;

	@Operation(summary = "MCP 리뷰 조회", description = "해당 MCP의 리뷰를 조회한다")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
		@ApiResponse(responseCode = "404", description = "MCP가 존재하지않음")
	})
	@GetMapping("/{mcpId}")
	public BaseResponse<Page<McpReviewResponse>> getReviewList(@PathVariable Long mcpId, @ModelAttribute
	McpReviewListRequest request) {
		Pageable pageable = PageRequest.of(
			request.getPage(),
			Math.min(request.getSize(), 50),
			Sort.by(Sort.Direction.DESC, "id")
		);
		return BaseResponse.onSuccess(mcpReviewAdviser.getReviewList(mcpId, request, pageable));
	}

	@Operation(summary = "MCP 리뷰 저장", description = "해당 MCP의 리뷰를 저장한다")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
		@ApiResponse(responseCode = "404", description = "MCP가 존재하지않음")
	})
	@PostMapping("/{mcpId}")
	public BaseResponse<Long> saveReview(@PathVariable Long mcpId, @RequestBody McpReviewRequest request) {
		return BaseResponse.onSuccess(mcpReviewAdviser.saveReview(mcpId, request));
	}

	@Operation(summary = "MCP 리뷰 수정", description = "해당 MCP의 리뷰를 수정한다")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
		@ApiResponse(responseCode = "403", description = "접근 금지 (본인이 아닌 게시글을 수정 할때)"),
		@ApiResponse(responseCode = "404", description = "해당 리뷰가 존재하지않음")
	})
	@PatchMapping("/{reviewId}")
	public BaseResponse<Long> updateReview(@PathVariable Long reviewId, @RequestBody McpReviewRequest request) {
		return BaseResponse.onSuccess(mcpReviewAdviser.updateReview(reviewId, request));
	}

	@Operation(summary = "MCP 리뷰 삭제", description = "해당 MCP의 리뷰를 삭제한다")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
		@ApiResponse(responseCode = "403", description = "접근 금지 (본인이 아닌 게시글을 삭제 할때)"),
		@ApiResponse(responseCode = "404", description = "해당 리뷰가 존재하지않음")
	})
	@DeleteMapping("/{reviewId}")
	public BaseResponse<Long> deleteReview(@PathVariable Long reviewId) {
		return BaseResponse.onSuccess(mcpReviewAdviser.deleteReview(reviewId));

	}
}
