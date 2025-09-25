package com.mcphub;

import com.mcphub.domain.mcp.dto.request.McpDraftRequest;
import com.mcphub.domain.mcp.dto.request.McpListRequest;
import com.mcphub.domain.mcp.dto.request.McpUrlRequest;
import com.mcphub.domain.mcp.dto.request.MyUploadMcpRequest;
import com.mcphub.domain.mcp.dto.response.api.McpToolResponse;
import com.mcphub.domain.mcp.dto.response.readmodel.McpReadModel;
import com.mcphub.domain.mcp.dto.response.readmodel.MyUploadMcpDetailReadModel;
import com.mcphub.domain.mcp.entity.Mcp;
import com.mcphub.domain.mcp.entity.UserMcp;
import com.mcphub.domain.mcp.repository.jsp.ArticleMcpToolRepository;
import com.mcphub.domain.mcp.repository.jsp.CategoryRepository;
import com.mcphub.domain.mcp.repository.jsp.LicenseRepository;
import com.mcphub.domain.mcp.repository.jsp.McpRepository;
import com.mcphub.domain.mcp.repository.jsp.McpReviewRepository;
import com.mcphub.domain.mcp.repository.jsp.PlatformRepository;
import com.mcphub.domain.mcp.repository.jsp.UserMcpRepository;
import com.mcphub.domain.mcp.repository.querydsl.McpDslRepository;
import com.mcphub.domain.mcp.service.mcp.McpServiceImpl;
import com.mcphub.domain.mcp.service.mcpDashboard.McpDashboardServiceImpl;
import com.mcphub.global.common.exception.RestApiException;
import com.mcphub.global.common.exception.code.status.GlobalErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class McpDashboardServiceImplTest {

	@Mock
	private McpDslRepository mcpDslRepository;
	@Mock
	private McpRepository mcpRepository;
	@Mock
	private PlatformRepository platformRepository;
	@Mock
	private LicenseRepository licenseRepository;
	@Mock
	private CategoryRepository categoryRepository;
	@Mock
	private ArticleMcpToolRepository mcpToolRepository;
	@Mock
	private MultipartFile multipartFile;

	@InjectMocks
	private McpDashboardServiceImpl dashboardService;

	@Test
	@DisplayName("내가 업로드한 MCP 목록 조회")
	void getMyUploadMcpList_success() {
		// given
		Pageable pageable = Pageable.ofSize(10);
		McpListRequest request = new McpListRequest();
		Long userId = 1L;
		Page<McpReadModel> mockPage = Page.empty();

		given(mcpDslRepository.searchMyUploadMcps(request, pageable, userId))
			.willReturn(mockPage);

		// when
		Page<McpReadModel> result = dashboardService.getMyUploadMcpList(pageable, request, userId);

		// then
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("업로드한 MCP 상세 조회 성공")
	void getUploadMcpDetail_success() {
		// given
		Long userId = 1L;
		Long mcpId = 10L;
		Mcp mcp = Mcp.builder().id(mcpId).userId(userId).build();

		MyUploadMcpDetailReadModel rm = new MyUploadMcpDetailReadModel();
		rm.setId(mcpId);

		given(mcpRepository.findByIdAndDeletedAtIsNull(mcpId)).willReturn(Optional.of(mcp));
		given(mcpDslRepository.getMyUploadMcpDetail(mcpId)).willReturn(rm);
		given(mcpDslRepository.getMcpTools(mcpId)).willReturn(List.of());

		// when
		MyUploadMcpDetailReadModel result = dashboardService.getUploadMcpDetail(userId, mcpId);

		// then
		assertThat(result.getId()).isEqualTo(mcpId);
		verify(mcpDslRepository).getMcpTools(mcpId);
	}

	@Test
	@DisplayName("업로드한 MCP 상세 조회 실패 - 다른 사용자")
	void getUploadMcpDetail_fail_forbidden() {
		// given
		Mcp mcp = Mcp.builder().id(1L).userId(99L).build();
		given(mcpRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(mcp));

		// when & then
		assertThatThrownBy(() -> dashboardService.getUploadMcpDetail(1L, 1L))
			.isInstanceOf(RestApiException.class)
			.hasMessageContaining("FORBIDDEN");
	}

	@Test
	@DisplayName("MCP 임시 저장(Draft) 생성")
	void createMcpDraft_success() {
		// given
		Mcp mcp = Mcp.builder().id(1L).build();
		given(mcpRepository.save(any(Mcp.class))).willReturn(mcp);

		// when
		Long result = dashboardService.createMcpDraft(1L, new McpDraftRequest());

		// then
		assertThat(result).isEqualTo(1L);
		verify(mcpRepository).save(any(Mcp.class));
	}

	@Test
	@DisplayName("MCP URL 업로드 성공")
	void uploadMcpUrl_success() {
		// given
		Mcp mcp = Mcp.builder().id(1L).userId(1L).build();
		McpUrlRequest request = new McpUrlRequest();
		request.setUrl("http://test.com");

		given(mcpRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(mcp));
		given(mcpRepository.save(mcp)).willReturn(mcp);

		// when
		Long result = dashboardService.uploadMcpUrl(1L, 1L, request);

		// then
		assertThat(result).isEqualTo(1L);
		assertThat(mcp.getRequestUrl()).isEqualTo("http://test.com");
	}

	@Test
	@DisplayName("MCP URL 업로드 실패 - 다른 사용자")
	void uploadMcpUrl_fail_forbidden() {
		// given
		Mcp mcp = Mcp.builder().id(1L).userId(99L).build();
		McpUrlRequest request = new McpUrlRequest();
		request.setUrl("http://test.com");

		given(mcpRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(mcp));

		// when & then
		assertThatThrownBy(() -> dashboardService.uploadMcpUrl(1L, 1L, request))
			.isInstanceOf(RestApiException.class)
			.hasMessageContaining("FORBIDDEN");
	}

	@Test
	@DisplayName("MCP 삭제 성공")
	void deleteMcp_success() {
		// given
		Long userId = 1L;
		Long mcpId = 10L;
		Mcp mcp = Mcp.builder().id(mcpId).userId(userId).build();

		given(mcpRepository.findByIdAndDeletedAtIsNull(mcpId)).willReturn(Optional.of(mcp));
		given(mcpRepository.save(mcp)).willReturn(mcp);

		// when
		Long result = dashboardService.deleteMcp(userId, mcpId);

		// then
		assertThat(result).isEqualTo(mcpId);
		verify(mcpRepository).save(mcp);
	}

	@Test
	@DisplayName("MCP 삭제 실패 - 다른 사용자")
	void deleteMcp_fail_forbidden() {
		// given
		Mcp mcp = Mcp.builder().id(1L).userId(99L).build();
		given(mcpRepository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(mcp));

		// when & then
		assertThatThrownBy(() -> dashboardService.deleteMcp(1L, 1L))
			.isInstanceOf(RestApiException.class)
			.hasMessageContaining("FORBIDDEN");
	}
}
