package com.mcphub.domain.mcp.converter;

import com.mcphub.domain.mcp.dto.response.api.McpDetailResponse;
import com.mcphub.domain.mcp.dto.response.api.McpResponse;
import com.mcphub.domain.mcp.dto.response.api.MySavedMcpResponse;
import com.mcphub.domain.mcp.dto.response.readmodel.McpReadModel;
import org.springframework.stereotype.Component;
import com.mcphub.domain.mcp.dto.response.api.TestResponse;

@Component
public class McpConverter {

	public TestResponse toTestResponse(Long id) {
		return TestResponse.builder()
		                   .id(id)
		                   .build();
	}

	public McpResponse toMcpResponse(McpReadModel m) {
		return McpResponse.builder()
		                  .id(m.getId())
		                  .name(m.getName())
		                  .version(m.getVersion())
		                  .description(m.getDescription())
		                  .imageUrl(m.getImageUrl())
		                  .isKeyRequired(m.getIsKeyRequired())
		                  .categoryName(m.getCategoryName())
		                  .platformName(m.getPlatformName())
		                  .developerName(m.getDeveloperName())
		                  .licenseName(m.getLicenseName())
		                  .averageRating(m.getAverageRating())
		                  .savedUserCount(m.getSavedUserCount())
		                  .publishedDate(m.getPublishedAt())
		                  .build();
	}

	public McpDetailResponse toMcpDetailResponse(McpReadModel m) {
		return McpDetailResponse.builder()
		                        .id(m.getId())
		                        .name(m.getName())
		                        .version(m.getVersion())
		                        .description(m.getDescription())
		                        .imageUrl(m.getImageUrl())
		                        .isKeyRequired(m.getIsKeyRequired())
		                        .categoryName(m.getCategoryName())
		                        .platformName(m.getPlatformName())
                                .developerName(m.getDeveloperName())
                                .requestUrl(m.getRequestUrl())
                                .sourceUrl(m.getSourceUrl())
                                .publishDate(m.getPublishedAt().toLocalDate())
		                        .licenseName(m.getLicenseName())
		                        .averageRating(m.getAverageRating())
		                        .savedUserCount(m.getSavedUserCount())
		                        .tools(m.getTools())
		                        .build();
	}

	public MySavedMcpResponse toMyUploadMcpResponse(McpReadModel m) {
		return MySavedMcpResponse.builder()
		                         .id(m.getId())
		                         .name(m.getName())
		                         .version(m.getVersion())
		                         .description(m.getDescription())
		                         .imageUrl(m.getImageUrl())
		                         .categoryName(m.getCategoryName())
		                         .platformName(m.getPlatformName())
		                         .licenseName(m.getLicenseName())
		                         .build();
	}
}
