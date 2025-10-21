package com.mcphub.domain.mcp.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McpReviewRequest {

	//평점 입력이 없을때
	@NotNull(message = "평점을 입력해주세요.")
	@Min(value = 0, message = "평점은 0 이상이어야 합니다.")
	@Max(value = 5, message = "평점은 5 이하여야 합니다.")
	private Double rating;

	@NotBlank(message = "리뷰 내용을 입력해주세요.")
	private String comment;
}
