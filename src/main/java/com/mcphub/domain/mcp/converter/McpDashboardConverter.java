package com.mcphub.domain.mcp.converter;

import com.mcphub.domain.mcp.dto.response.api.McpResponse;
import com.mcphub.domain.mcp.dto.response.api.MyUploadMcpDetailResponse;
import com.mcphub.domain.mcp.dto.response.api.TestResponse;
import com.mcphub.domain.mcp.dto.response.readmodel.McpReadModel;
import com.mcphub.domain.mcp.dto.response.readmodel.MyUploadMcpDetailReadModel;
import org.springframework.stereotype.Component;

@Component
public class McpDashboardConverter {

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
		                  .licenseName(m.getLicenseName())
		                  .averageRating(m.getAverageRating())
		                  .savedUserCount(m.getSavedUserCount())
		                  .publishedDate(m.getPublishedAt())
		                  .build();
	}

	public MyUploadMcpDetailResponse toMyUploadMcpDetailResponse(MyUploadMcpDetailReadModel readModel) {
		if (readModel == null) {
			return null;
		}

		return MyUploadMcpDetailResponse.builder()
		                                .id(readModel.getId())
		                                .name(readModel.getName())
		                                .version(readModel.getVersion())
		                                .description(readModel.getDescription())
		                                .imageUrl(readModel.getImageUrl())
		                                .sourceUrl(readModel.getSourceUrl())
		                                .requestUrl(readModel.getRequestUrl())
		                                .developerName(readModel.getDeveloperName())
		                                .isKeyRequired(readModel.getIsKeyRequired())

		                                .categoryId(readModel.getCategoryId())
		                                .categoryName(readModel.getCategoryName())
		                                .platformId(readModel.getPlatformId())
		                                .platformName(readModel.getPlatformName())
		                                .licenseId(readModel.getLicenseId())
		                                .licenseName(readModel.getLicenseName())
		                                .isPublished(readModel.isPublished())
		                                .publishedAt(readModel.getPublishedAt())
		                                .lastPublishedAt(readModel.getLastPublishedAt())

		                                .tools(readModel.getTools())
		                                .build();
	}
}
