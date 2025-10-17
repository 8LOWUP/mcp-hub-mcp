package com.mcphub;

import com.mcphub.domain.mcp.dto.request.McpListRequest;
import com.mcphub.domain.mcp.dto.request.MyUploadMcpRequest;
import com.mcphub.domain.mcp.dto.response.api.McpSaveResponse;
import com.mcphub.domain.mcp.dto.response.api.McpToolResponse;
import com.mcphub.domain.mcp.dto.response.readmodel.McpReadModel;
import com.mcphub.domain.mcp.entity.Mcp;
import com.mcphub.domain.mcp.entity.UserMcp;
import com.mcphub.domain.mcp.repository.jsp.McpRepository;
import com.mcphub.domain.mcp.repository.jsp.McpReviewRepository;
import com.mcphub.domain.mcp.repository.jsp.UserMcpRepository;
import com.mcphub.domain.mcp.repository.querydsl.McpDslRepository;
import com.mcphub.domain.mcp.service.mcp.McpServiceImpl;
import com.mcphub.global.common.exception.RestApiException;
import com.mcphub.global.common.exception.code.status.GlobalErrorStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class McpServiceImplTests {

	@Mock
	private McpRepository mcpRepository;
	@Mock
	private McpReviewRepository mcpReviewRepository;
	@Mock
	private UserMcpRepository userMcpRepository;
	@Mock
	private McpDslRepository mcpDslRepository;

	@InjectMocks
	private McpServiceImpl mcpService;

	@Test
	public void MCP_상세조회_성공() {
		// given
		Long mcpId = 1L;
		McpReadModel mockReadModel = McpReadModel.builder().id(mcpId).name("테스트MCP").build();
		List<McpToolResponse> mockTools = List.of(new McpToolResponse(1L, "Tool1", "content"));

		given(mcpDslRepository.getMcpDetail(mcpId)).willReturn(mockReadModel);
		given(mcpDslRepository.getMcpTools(mcpId)).willReturn(mockTools);

		// when
		McpReadModel result = mcpService.getMcpDetail(mcpId, 1L);

		// then
		assertThat(result.getId()).isEqualTo(mcpId);
		assertThat(result.getTools()).hasSize(1);
	}

	@Test
	public void MCP상세조회_없으면_NOT_FOUND() {
		// given
		Long mcpId = 1L;
		given(mcpDslRepository.getMcpDetail(mcpId)).willReturn(null);

		// when & then
		assertThatThrownBy(() -> mcpService.getMcpDetail(mcpId, 1L))
			.isInstanceOf(RestApiException.class)
			.hasMessageContaining(GlobalErrorStatus._NOT_FOUND.getMessage());
	}

	@Test
	public void MCP리스트조회_성공() {
		// given
		McpListRequest req = new McpListRequest();
		Pageable pageable = PageRequest.of(0, 10);
		McpReadModel mockReadModel = McpReadModel.builder().id(1L).name("리스트MCP").build();

		Page<McpReadModel> mockPage = new PageImpl<>(List.of(mockReadModel), pageable, 1);
		given(mcpDslRepository.searchMcps(req, pageable)).willReturn(mockPage);

		// when
		Page<McpReadModel> result = mcpService.getMcpList(pageable, req);

		// then
		assertThat(result.getContent()).hasSize(1);
	}

	@Test
	public void MCP저장_이미저장된경우_예외() {
		// given
		Long userId = 1L;
		Long mcpId = 1L;
		given(userMcpRepository.existsByUserIdAndMcpId(userId, mcpId)).willReturn(true);

		// when & then
		assertThatThrownBy(() -> mcpService.saveUserMcp(userId, mcpId))
			.isInstanceOf(RestApiException.class)
			.hasMessageContaining(GlobalErrorStatus._ALREADY_SAVED_MCP.getMessage());
	}

	@Test
	public void MCP저장_미발행된경우_예외() {
		// given
		Long userId = 1L;
		Long mcpId = 1L;
		Mcp unpublished = Mcp.builder().id(mcpId).isPublished(false).build();

		given(userMcpRepository.existsByUserIdAndMcpId(userId, mcpId)).willReturn(false);
		given(mcpRepository.findByIdAndDeletedAtIsNull(mcpId)).willReturn(Optional.of(unpublished));

		// when & then
		assertThatThrownBy(() -> mcpService.saveUserMcp(userId, mcpId))
			.isInstanceOf(RestApiException.class)
			.hasMessageContaining(GlobalErrorStatus._VALIDATION_ERROR.getMessage());
	}

	@Test
	public void MCP저장_성공() {
		// given
		Long userId = 1L;
		Long mcpId = 1L;
		Mcp published = Mcp.builder().id(mcpId).isPublished(true).build();
		UserMcp mockUserMcp = UserMcp.builder().id(10L).userId(userId).mcp(published).build();

		given(userMcpRepository.existsByUserIdAndMcpId(userId, mcpId)).willReturn(false);
		given(mcpRepository.findByIdAndDeletedAtIsNull(mcpId)).willReturn(Optional.of(published));
		given(userMcpRepository.save(any(UserMcp.class))).willReturn(mockUserMcp);

		// when
		McpSaveResponse resultId = mcpService.saveUserMcp(userId, mcpId);

		// then
		assertThat(resultId).isEqualTo(10L);
	}

	@Test
	public void MCP삭제_성공() {
		// given
		Long userId = 1L;
		Long mcpId = 1L;
		Mcp mockMcp = Mcp.builder().id(mcpId).build();
		given(mcpRepository.findById(mcpId)).willReturn(Optional.of(mockMcp));

		// when
		Long result = mcpService.deleteMcp(userId, mcpId);

		// then
		assertThat(result).isEqualTo(mcpId);
		verify(userMcpRepository).deleteByUserIdAndMcp(userId, mockMcp);
	}

	@Test
	public void 내가저장한MCP리스트조회_성공() {
		// given
		Long userId = 1L;
		Pageable pageable = PageRequest.of(0, 10);
		Mcp mockMcp = Mcp.builder().id(1L).name("내MCP").build();
		UserMcp mockUserMcp = UserMcp.builder().id(10L).userId(userId).mcp(mockMcp).build();

		Page<UserMcp> mockPage = new PageImpl<>(List.of(mockUserMcp), pageable, 1);
		given(userMcpRepository.findByUserId(userId, pageable)).willReturn(mockPage);

		// when
		Page<McpReadModel> result = mcpService.getMySavedMcpList(userId, pageable, new MyUploadMcpRequest());

		// then
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
	}
}
