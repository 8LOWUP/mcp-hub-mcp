package com.mcphub.domain.mcp.dto.response.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McpDetailResponse {
	private Long id;
	private String name;
	private String version;
	private String description;
	private String requestUrl;
	private String sourceUrl;
	private String imageUrl;
	private Boolean isKeyRequired;
	private String developerName;
	private String categoryName;
	private String platformName;
	private String licenseName;
	private boolean alreadySaved;
	private Double averageRating;
	private Integer savedUserCount;

	private List<McpToolResponse> tools;

	//최근 Mcp 업데이트 된 날
	private LocalDate publishDate;
	// 첫 배포 등록일
	private LocalDate lastPublishDate;
}
