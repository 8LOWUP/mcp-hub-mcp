package com.mcphub.domain.mcp.controller;

import com.mcphub.domain.mcp.adviser.RecommendationAdviser;
import com.mcphub.domain.mcp.dto.request.McpReviewListRequest;
import com.mcphub.domain.mcp.dto.request.RecommendationRequest;
import com.mcphub.domain.mcp.dto.response.api.McpReviewResponse;
import com.mcphub.domain.mcp.dto.response.api.RecommendationResponse;
import com.mcphub.global.common.base.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mcps/recommend")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationAdviser recommendationAdviser;

    @Operation(summary = "추천 기능 채팅", description = "mcp 추천을 요청하는 채팅을 보내고 답변을 받습니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 내부 처리 오류")
    })
    @PostMapping()
    public BaseResponse<RecommendationResponse> recommendationChat(@RequestBody RecommendationRequest request) {
        return BaseResponse.onSuccess(recommendationAdviser.recommendationChat(request));
    }
}
