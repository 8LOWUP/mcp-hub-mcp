package com.mcphub.domain.mcp.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyUploadMcpRequest {
	@Min(value = 0, message = "페이지 번호는 0보다 작을 수 없습니다.")
	private int page = 0;
	@Min(value = 1, message = "사이즈 크기는 1보다 작을 수 없습니다.")
	private int size = 12;
	private String search;
	private String sort = "latest";
}
