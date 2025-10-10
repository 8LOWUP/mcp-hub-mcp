package com.mcphub.domain.mcp.service.mcpDashboard;

import com.mcphub.domain.mcp.dto.request.McpDraftRequest;
import com.mcphub.domain.mcp.dto.request.McpListRequest;
import com.mcphub.domain.mcp.dto.request.McpUploadDataRequest;
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
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class McpDashboardServiceImpl implements McpDashboardService {

	//TODO prod 올릴 때 해당 경로 존재해야함 + yml 수정
	@Value("${spring.file.upload-dir}")
	private String uploadDir;

	private final McpDslRepository mcpDslRepository;
	private final McpRepository mcpRepository;
	private final PlatformRepository platformRepository;
	private final LicenseRepository licenseRepository;
	private final CategoryRepository categoryRepository;
	private final ArticleMcpToolRepository mcpToolRepository;
	//private final ProducerService producerService;

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
		mcp.setName(request.getName());
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
	public Long uploadMcpMetaData(Long userId, McpUploadDataRequest request, MultipartFile file) {
		Mcp mcp;
		if (request.getMcpId() == null) {
			mcp = mcpRepository.save(Mcp.builder().userId(userId).build());
		} else {
			mcp = mcpRepository.findByIdAndDeletedAtIsNull(request.getMcpId())
			                   .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));
			if (!mcp.getUserId().equals(userId)) {
				throw new RestApiException(GlobalErrorStatus._FORBIDDEN);
			}
		}

		try {
			if (file != null && !file.isEmpty()) {
				// 원본 파일명에서 확장자 추출
				String originalName = file.getOriginalFilename();   // 예: "logo.png"
				String ext = "";
				if (originalName != null && originalName.lastIndexOf(".") != -1) {
					ext = originalName.substring(originalName.lastIndexOf(".")); // ".png"
				}

				// mcpId 기반 저장 파일명 생성
				String fileName = mcp.getId().toString() + ext;
				mcp.setImageUrl("/mcps/images/" + fileName);

				// 업로드 경로 (yml에 file:/ 로 돼 있으니 prefix 제거)
				File directory = new File(uploadDir.replace("file:", ""));
				if (!directory.exists()) {
					directory.mkdirs();
				}

				// 최종 파일 저장
				File dest = new File(directory, fileName);
				file.transferTo(dest);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RestApiException(GlobalErrorStatus._INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RestApiException(GlobalErrorStatus._INTERNAL_SERVER_ERROR);
		}

		Category category = categoryRepository.findById(request.getCategoryId())
		                                      .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));
		Platform platform = platformRepository.findByName(request.getPlatformName())
		                                      .orElse(null);
		if (platform == null) {
			platform = new Platform();
			platform.setName(request.getPlatformName());
			platformRepository.save(platform);
		}
		License license = licenseRepository.findById(request.getLicenseId())
		                                   .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

		mcp.setName(request.getName());
		//mcp.setVersion(request.getVersion());
		mcp.setDescription(request.getDescription());
		mcp.setIsPublished(false);
		mcp.setSourceUrl(request.getSourceUrl());
		mcp.setRequestUrl(request.getRequestUrl());
		mcp.setDeveloperName(request.getDeveloperName());
		mcp.setIsKeyRequired(request.getIsKeyRequired());
		mcp.setCategory(category);
		mcp.setPlatform(platform);
		mcp.setLicense(license);

		if (request.getTools() == null) {
			mcpToolRepository.deleteByMcp(mcp);
		} else {
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
		// if (isChanged && TransactionSynchronizationManager.isSynchronizationActive()) {
		// 	TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
		// 		@Override
		// 		public void afterCommit() {
		// 			producerService.sendUrlDeletedEvent(mcpId);
		// 		}
		// 	});
		// }
		return mcp.getId();
	}

	@Override
	@Transactional
	public Long publishMcp(Long userId, McpUploadDataRequest request, MultipartFile file) {
		Mcp mcp;
		if (request.getMcpId() == null) {
			mcp = mcpRepository.save(Mcp.builder().userId(userId).build());
		} else {
			mcp = mcpRepository.findByIdAndDeletedAtIsNull(request.getMcpId())
			                   .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));
			if (!mcp.getUserId().equals(userId)) {
				throw new RestApiException(GlobalErrorStatus._FORBIDDEN);
			}
		}
		try {
			if (file != null && !file.isEmpty()) {
				// 원본 파일명에서 확장자 추출
				String originalName = file.getOriginalFilename();   // 예: "logo.png"
				String ext = "";
				if (originalName != null && originalName.lastIndexOf(".") != -1) {
					ext = originalName.substring(originalName.lastIndexOf(".")); // ".png"
				}

				// mcpId 기반 저장 파일명 생성
				String fileName = mcp.getId().toString() + ext;
				mcp.setImageUrl("/mcps/images/" + fileName);

				// 업로드 경로 (yml에 file:/ 로 돼 있으니 prefix 제거)
				File directory = new File(uploadDir.replace("file:", ""));
				if (!directory.exists()) {
					directory.mkdirs();
				}

				// 최종 파일 저장
				File dest = new File(directory, fileName);
				file.transferTo(dest);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RestApiException(GlobalErrorStatus._INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RestApiException(GlobalErrorStatus._INTERNAL_SERVER_ERROR);
		}

		Category category = categoryRepository.findById(request.getCategoryId())
		                                      .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));
		Platform platform = platformRepository.findByName(request.getPlatformName())
		                                      .orElse(null);
		if (platform == null) {
			platform = new Platform();
			platform.setName(request.getPlatformName());
			platformRepository.save(platform);
		}

		License license = licenseRepository.findById(request.getLicenseId())
		                                   .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

		mcp.setName(request.getName());
		//mcp.setVersion(request.getVersion());
		mcp.setDescription(request.getDescription());
		mcp.setIsPublished(true);
		mcp.setSourceUrl(request.getSourceUrl());
		mcp.setDeveloperName(request.getDeveloperName());
		mcp.setRequestUrl(request.getRequestUrl());
		mcp.setIsKeyRequired(request.getIsKeyRequired());
		mcp.setCategory(category);
		mcp.setPlatform(platform);
		mcp.setLicense(license);
		LocalDateTime now = LocalDateTime.now();
		if (mcp.getPublishedAt() == null) {
			mcp.setPublishedAt(now);
		}
		mcp.setLastPublishedAt(now);

		if (request.getTools() == null) {
			mcpToolRepository.deleteByMcp(mcp);
		} else {
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
		//URL NPE 발생가능성있음...
		// if (isChanged || !mcp.getRequestUrl().equals(request.getRequestUrl())) {
		// 	if (TransactionSynchronizationManager.isSynchronizationActive()) {
		// 		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
		// 			@Override
		// 			public void afterCommit() {
		// 				producerService.sendUrlSavedEvent(mcpId, mcp.getRequestUrl());
		// 			}
		// 		});
		// 	}
		// }
		return mcpRepository.save(mcp).getId();
	}

	@Override
	@Transactional
	public Long deleteMcp(Long userId, Long mcpId) {

		Mcp mcp = mcpRepository.findByIdAndDeletedAtIsNull(mcpId)
		                       .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

		if (!mcp.getUserId().equals(userId)) {
			//TODO : 본인 소유가 아닌 MCP 접근에 대한 에러 코드를 재작성할 필요가 있어보임
			throw new RestApiException(GlobalErrorStatus._FORBIDDEN);
		}

		mcp.delete();
		// if (TransactionSynchronizationManager.isSynchronizationActive()) {
		// 	TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
		// 		@Override
		// 		public void afterCommit() {
		// 			producerService.sendUrlDeletedEvent(mcpId);
		// 		}
		// 	});
		// }
		return mcpRepository.save(mcp).getId();
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> getPlatform() {
		return platformRepository.findAll().stream().map(Platform::getName).collect(Collectors.toList());
	}
}
