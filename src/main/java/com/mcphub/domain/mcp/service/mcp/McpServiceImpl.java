package com.mcphub.domain.mcp.service.mcp;

import com.mcphub.domain.error.McpErrorStatus;
import com.mcphub.domain.mcp.dto.request.McpListRequest;
import com.mcphub.domain.mcp.dto.request.MyUploadMcpRequest;
import com.mcphub.domain.mcp.dto.response.api.McpToolResponse;
import com.mcphub.domain.mcp.dto.response.readmodel.McpReadModel;
import com.mcphub.domain.mcp.entity.UserMcp;
import com.mcphub.domain.mcp.repository.jsp.UserMcpRepository;
import com.mcphub.domain.mcp.repository.querydsl.McpDslRepository;
import com.mcphub.global.common.exception.RestApiException;
import com.mcphub.global.common.exception.code.status.GlobalErrorStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.mcphub.domain.mcp.entity.Mcp;
import com.mcphub.domain.mcp.repository.jsp.McpRepository;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
@RequiredArgsConstructor
public class McpServiceImpl implements McpService {

	private final McpRepository mcpRepository;
	private final UserMcpRepository userMcpRepository;
	private final McpDslRepository mcpDslRepository;
	//private final ProducerService producerService;

	@Override
	@Transactional(readOnly = true)
	public McpReadModel getMcpDetail(Long id) {
		McpReadModel rm = mcpDslRepository.getMcpDetail(id);
		if (rm == null) {
			throw new RestApiException(McpErrorStatus._NOT_FOUND_INFO);
		}
		List<McpToolResponse> tools = mcpDslRepository.getMcpTools(id);
		rm.setTools(tools);
		return rm;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<McpReadModel> getMcpList(Pageable pageable, McpListRequest req) {
		return mcpDslRepository.searchMcps(req, pageable);
	}

	@Override
	@Transactional
	public Long saveUserMcp(Long userId, Long mcpId) {
		boolean exists = userMcpRepository.existsByUserIdAndMcpId(userId, mcpId);
		if (exists) {
			throw new RestApiException(McpErrorStatus._ALREADY_SAVED_MCP);
		}

		Mcp mcp = mcpRepository.findByIdAndDeletedAtIsNull(mcpId)
		                       .orElseThrow(() -> new RestApiException(McpErrorStatus._NOT_FOUND));

		if (!mcp.getIsPublished()) {
			throw new RestApiException(McpErrorStatus._NOT_PUBLISH);
		}
		UserMcp newUserMcp = UserMcp.builder()
		                            .userId(userId)
		                            .mcp(mcp)
		                            .platformId(mcp.getPlatform().getId())
		                            .build();

		UserMcp saved = userMcpRepository.save(newUserMcp);

		// if (TransactionSynchronizationManager.isSynchronizationActive()) {
		// 	TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
		// 		@Override
		// 		public void afterCommit() {
		// 			producerService.sendMcpSavedEvent(userId, mcpId);
		// 		}
		// 	});
		// }

		return saved.getId();
	}

	// 구매한 Mcp삭제
	@Override
	@Transactional
	public Long deleteMcp(Long userId, Long mcpId) {
		Mcp mcp = mcpRepository.findById(mcpId)
		                       .orElseThrow(() -> new RestApiException(McpErrorStatus._NOT_FOUND));
		int deleted = userMcpRepository.deleteByUserIdAndMcp(userId, mcp);
		if (deleted == 0) {
			throw new RestApiException(McpErrorStatus._ALREADY_DELETED_MCP);
		}
		// if (TransactionSynchronizationManager.isSynchronizationActive()) {
		// 	TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
		// 		@Override
		// 		public void afterCommit() {
		// 			producerService.sendMcpDeletedEvent(userId, mcpId);
		// 		}
		// 	});
		// }
		return mcp.getId();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<McpReadModel> getMySavedMcpList(Long userId, Pageable pageable, MyUploadMcpRequest request) {

		Page<UserMcp> userMcpPage = userMcpRepository.findByUserId(userId, pageable);

		return userMcpPage.map(userMcp -> {
			Mcp mcp = userMcp.getMcp();
			return McpReadModel.builder()
			                   .id(mcp.getId())
			                   .name(mcp.getName())
			                   .version(mcp.getVersion())
			                   .description(mcp.getDescription())
			                   .imageUrl(mcp.getImageUrl())
			                   .categoryName(mcp.getCategory().getName())
			                   .platformName(mcp.getPlatform().getName())
			                   .licenseName(mcp.getLicense().getName())
			                   .createdAt(userMcp.getCreatedAt())
			                   .build();
		});

	}

}
