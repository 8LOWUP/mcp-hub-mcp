package com.mcphub.domain.mcp.service.mcpDashboard;

import com.mcphub.domain.mcp.dto.request.McpDraftRequest;
import com.mcphub.domain.mcp.dto.request.McpListRequest;
import com.mcphub.domain.mcp.dto.request.McpMetaDataRequest;
import com.mcphub.domain.mcp.dto.request.McpPublishRequest;
import com.mcphub.domain.mcp.dto.request.McpUrlRequest;
import com.mcphub.domain.mcp.dto.response.api.McpToolResponse;
import com.mcphub.domain.mcp.dto.response.readmodel.McpReadModel;
import com.mcphub.domain.mcp.dto.response.readmodel.MyUploadMcpDetailReadModel;
import com.mcphub.domain.mcp.entity.ArticleMcpTool;
import com.mcphub.domain.mcp.entity.Category;
import com.mcphub.domain.mcp.entity.License;
import com.mcphub.domain.mcp.entity.Mcp;
import com.mcphub.domain.mcp.entity.Platform;
import com.mcphub.domain.mcp.repository.jsp.ArticleMcpToolRepository;
import com.mcphub.domain.mcp.repository.jsp.CategoryRepository;
import com.mcphub.domain.mcp.repository.jsp.LicenseRepository;
import com.mcphub.domain.mcp.repository.jsp.McpRepository;
import com.mcphub.domain.mcp.repository.jsp.PlatformRepository;
import com.mcphub.domain.mcp.repository.querydsl.McpDslRepository;
import com.mcphub.global.common.exception.RestApiException;
import com.mcphub.global.common.exception.code.status.GlobalErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class McpDashboardServiceImpl implements McpDashboardService {

	private final McpDslRepository mcpDslRepository;
	private final McpRepository mcpRepository;
	private final PlatformRepository platformRepository;
	private final LicenseRepository licenseRepository;
	private final CategoryRepository categoryRepository;
	private final ArticleMcpToolRepository mcpToolRepository;

	@Override
	@Transactional(readOnly = true)
	public Page<McpReadModel> getMyUploadMcpList(Pageable pageable, McpListRequest request, Long userId) {
		return mcpDslRepository.searchMyUploadMcps(request, pageable, userId);
	}

	@Override
	@Transactional(readOnly = true)
	public MyUploadMcpDetailReadModel getUploadMcpDetail(Long userId, Long mcpId) {
		Mcp mcp = mcpRepository.findByIdAndDeletedAtIsNull(mcpId)
		                       .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

		if (!mcp.getUserId().equals(userId)) {
			throw new RestApiException(GlobalErrorStatus._FORBIDDEN);
		}

		MyUploadMcpDetailReadModel rm = mcpDslRepository.getMyUploadMcpDetail(mcpId);
		if (rm == null) {
			throw new RestApiException((GlobalErrorStatus._NOT_FOUND));
		}
		List<McpToolResponse> tools = mcpDslRepository.getMcpTools(mcpId);
		rm.setTools(tools);
		return rm;
	}

	@Override
	@Transactional
	public Long createMcpDraft(Long userId, McpDraftRequest request) {
		Mcp mcp = new Mcp();
		mcp.setUserId(userId);
		mcp.setName(request.getTitle());
		mcp.setIsPublished(false);
		return mcpRepository.save(mcp).getId();
	}

	@Override
	@Transactional
	public Long uploadMcpUrl(Long userId, Long mcpId, McpUrlRequest request) {
		Mcp mcp = mcpRepository.findByIdAndDeletedAtIsNull(mcpId)
		                       .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

		if (!mcp.getUserId().equals(userId)) {
			throw new RestApiException(GlobalErrorStatus._FORBIDDEN);
		}

		mcp.setRequestUrl(request.getUrl());
		return mcpRepository.save(mcp).getId();
	}

	@Override
	@Transactional
	public Long uploadMcpMetaData(Long userId, Long mcpId, McpMetaDataRequest request) {

		Mcp mcp = mcpRepository.findByIdAndDeletedAtIsNull(mcpId)
		                       .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

		if (!mcp.getUserId().equals(userId)) {
			throw new RestApiException(GlobalErrorStatus._FORBIDDEN);
		}

		Category category = categoryRepository.findById(request.getCategoryId())
		                                      .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));
		Platform platform = platformRepository.findById(request.getPlatformId())
		                                      .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));
		License license = licenseRepository.findById(request.getLicenseId())
		                                   .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

		mcp.setName(request.getName());
		mcp.setVersion(request.getVersion());
		mcp.setDescription(request.getDescription());
		mcp.setImageUrl(request.getImageUrl());
		mcp.setIsKeyRequired(request.getIsKeyRequired());
		mcp.setCategory(category);
		mcp.setPlatform(platform);
		mcp.setLicense(license);

		if (request.getTools() == null) {
			mcpToolRepository.deleteByMcp(mcp);
		}
		else {
			mcpToolRepository.deleteByMcp(mcp);

			List<ArticleMcpTool> tools = request.getTools().stream()
			                                    .map(t -> ArticleMcpTool.builder()
			                                                            .mcp(mcp)
			                                                            .name(t.getName())
			                                                            .content(t.getContent())
			                                                            .build())
			                                    .toList();

			mcpToolRepository.saveAll(tools);
		}

		return mcp.getId();
	}

	@Override
	@Transactional
	public Long publishMcp(Long userId, Long mcpId, McpPublishRequest request) {
		Mcp mcp = mcpRepository.findByIdAndDeletedAtIsNull(mcpId)
		                       .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

		if (!mcp.getUserId().equals(userId)) {
			throw new RestApiException(GlobalErrorStatus._FORBIDDEN);
		}

		//공개 처리
		if (request.isPublish()) {
			mcp.setIsPublished(true);
			mcp.setLastPublishAt(LocalDateTime.now());

			if (mcp.getPublishedAt() == null) {
				mcp.setPublishedAt(LocalDateTime.now());
			}
		}
		// 비공개 처리
		else {
			mcp.setIsPublished(false);
		}

		return mcpRepository.save(mcp).getId();
	}

	@Override
	@Transactional
	public Long deleteMcp(Long userId, Long mcpId) {

		Mcp mcp = mcpRepository.findByIdAndDeletedAtIsNull(mcpId)
		                       .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

		if (!mcp.getUserId().equals(userId)) {
			throw new RestApiException(GlobalErrorStatus._FORBIDDEN);
		}

		mcp.delete();

		return mcpRepository.save(mcp).getId();
	}
}
