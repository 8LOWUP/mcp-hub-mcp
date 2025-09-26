package com.mcphub;

import com.mcphub.domain.mcp.dto.request.McpReviewRequest;
import com.mcphub.domain.mcp.entity.McpReview;
import com.mcphub.domain.mcp.grpc.MemberGrpcClient;
import com.mcphub.domain.mcp.repository.jsp.McpRepository;
import com.mcphub.domain.mcp.repository.jsp.McpReviewRepository;
import com.mcphub.domain.mcp.service.mcpReview.McpReviewServiceImpl;
import com.mcphub.global.common.exception.RestApiException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import com.mcphub.domain.mcp.entity.Mcp;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class McpReviewServiceImplTest {

	@Mock
	private McpReviewRepository mcpReviewRepository;

	@Mock
	private McpRepository mcpRepository;

	@Mock
	private MemberGrpcClient memberGrpcClient;

	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@InjectMocks
	private McpReviewServiceImpl reviewService;

	@Test
	@DisplayName("리뷰 저장 성공")
	void saveReview_success() {
		// given
		Long userId = 1L;
		Long mcpId = 100L;

		Mcp mcp = Mcp.builder().id(mcpId).build();
		McpReviewRequest request = new McpReviewRequest();
		request.setRating(3);

		McpReview review = McpReview.builder()
		                            .id(200L)
		                            .mcp(mcp)
		                            .userId(userId)
		                            .rating(5.0)
		                            .build();

		given(mcpRepository.findByIdAndDeletedAtIsNull(mcpId)).willReturn(Optional.of(mcp));
		given(mcpReviewRepository.save(any(McpReview.class))).willReturn(review);

		// when
		Long result = reviewService.saveReview(userId, mcpId, request);

		// then
		assertThat(result).isEqualTo(200L);
		verify(mcpReviewRepository).save(any(McpReview.class));
	}

	@Test
	@DisplayName("리뷰 저장 실패 - MCP 없음")
	void saveReview_fail_mcpNotFound() {
		// given
		Long userId = 1L;
		Long mcpId = 999L;
		McpReviewRequest request = new McpReviewRequest();
		request.setRating(4);

		given(mcpRepository.findByIdAndDeletedAtIsNull(mcpId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> reviewService.saveReview(userId, mcpId, request))
			.isInstanceOf(RestApiException.class)
			.hasMessageContaining("NOT_FOUND");
	}

	@Test
	@DisplayName("리뷰 수정 성공")
	void updateReview_success() {
		// given
		Long userId = 1L;
		Long reviewId = 10L;
		McpReviewRequest request = new McpReviewRequest();
		request.setRating(3);
		request.setComment("수정된 내용");

		McpReview review = McpReview.builder()
		                            .id(reviewId)
		                            .userId(userId)
		                            .rating(5.0)
		                            .content("이전 내용")
		                            .build();

		given(mcpReviewRepository.findByIdAndDeletedAtIsNull(reviewId)).willReturn(Optional.of(review));
		given(mcpReviewRepository.save(any(McpReview.class))).willReturn(review);

		// when
		Long result = reviewService.updateReview(userId, reviewId, request);

		// then
		assertThat(result).isEqualTo(reviewId);
		assertThat(review.getContent()).isEqualTo("수정된 내용");
	}

	@Test
	@DisplayName("리뷰 수정 실패 - 작성자가 아님")
	void updateReview_fail_forbidden() {
		// given
		Long reviewId = 10L;
		McpReviewRequest request = new McpReviewRequest();
		request.setRating(4);
		request.setComment("수정");

		McpReview review = McpReview.builder()
		                            .id(reviewId)
		                            .userId(99L) // 다른 사용자
		                            .build();

		given(mcpReviewRepository.findByIdAndDeletedAtIsNull(reviewId)).willReturn(Optional.of(review));

		// when & then
		assertThatThrownBy(() -> reviewService.updateReview(1L, reviewId, request))
			.isInstanceOf(RestApiException.class)
			.hasMessageContaining("FORBIDDEN");
	}

	@Test
	@DisplayName("리뷰 삭제 성공")
	void deleteReview_success() {
		// given
		Long userId = 1L;
		Long reviewId = 10L;
		McpReview review = McpReview.builder().id(reviewId).userId(userId).build();

		given(mcpReviewRepository.findByIdAndDeletedAtIsNull(reviewId)).willReturn(Optional.of(review));

		// when
		Long result = reviewService.deleteReview(userId, reviewId);

		// then
		assertThat(result).isEqualTo(reviewId);
		verify(mcpReviewRepository).delete(review);
	}

	@Test
	@DisplayName("유저 이름 조회 - Redis 캐시 히트")
	void getUserName_fromCache() {
		// given
		Long userId = 1L;
		ValueOperations<String, Object> valueOps = mock(ValueOperations.class);
		given(redisTemplate.opsForValue()).willReturn(valueOps);
		given(valueOps.get("1")).willReturn("테스트유저");

		// when
		String userName = invokeGetUserName(userId);

		// then
		assertThat(userName).isEqualTo("테스트유저");
		verify(memberGrpcClient, never()).getUserName(anyLong());
	}

	@Test
	@DisplayName("유저 이름 조회 - Redis에 없을 때 gRPC 호출")
	void getUserName_fromGrpc() {
		// given
		Long userId = 1L;
		ValueOperations<String, Object> valueOps = mock(ValueOperations.class);
		given(redisTemplate.opsForValue()).willReturn(valueOps);
		given(valueOps.get("1")).willReturn(null);
		given(memberGrpcClient.getUserName(userId)).willReturn("gRPC유저");

		// when
		String userName = invokeGetUserName(userId);

		// then
		assertThat(userName).isEqualTo("gRPC유저");
		verify(memberGrpcClient).getUserName(userId);
	}

	// private 메서드 getUserName 테스트용 리플렉션
	private String invokeGetUserName(Long userId) {
		return org.springframework.test.util.ReflectionTestUtils
			.invokeMethod(reviewService, "getUserName", userId);
	}
}
