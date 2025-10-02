package com.mcphub.domain.mcp.dto.request;

import jakarta.validation.constraints.Min;
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
public class McpListRequest {

	@Min(value = 0, message = "페이지 번호는 0보다 작을 수 없습니다.")
	private Integer page = 0;
	//TODO : FE 요구사항 확인 필요

	@Min(value = 1, message = "사이즈 크기는 번호는 1보다 작을 수 없습니다.")
	private Integer size = 12;
	private String sort;
	private String category;
	private String search;
}
