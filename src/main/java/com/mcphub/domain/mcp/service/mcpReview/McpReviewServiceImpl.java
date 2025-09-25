package com.mcphub.domain.mcp.service.mcpReview;

import com.mcphub.domain.mcp.dto.request.McpReviewListRequest;
import com.mcphub.domain.mcp.dto.request.McpReviewRequest;
import com.mcphub.domain.mcp.dto.response.readmodel.McpReviewReadModel;
import com.mcphub.domain.mcp.entity.Mcp;
import com.mcphub.domain.mcp.entity.McpReview;
import com.mcphub.domain.mcp.grpc.MemberGrpcClient;
import com.mcphub.domain.mcp.repository.jsp.McpRepository;
import com.mcphub.domain.mcp.repository.jsp.McpReviewRepository;
import com.mcphub.domain.member.grpc.GetMemberInfoRequest;
import com.mcphub.domain.member.grpc.MemberResponse;
import com.mcphub.global.common.exception.RestApiException;
import com.mcphub.global.common.exception.code.status.GlobalErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class McpReviewServiceImpl implements McpReviewService {
	private final McpReviewRepository mcpReviewRepository;
	private final RedisTemplate<String, Object> redisTemplate;
	private final McpRepository mcpRepository;
	private final MemberGrpcClient memberGrpcClient;

	@Override
	@Transactional(readOnly = true)
	public Page<McpReviewReadModel> getMcpReviewList(Pageable pageable, McpReviewListRequest request, Long mcpId) {
		Mcp mcp = mcpRepository.findByIdAndDeletedAtIsNull(mcpId)
		                       .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));
		Page<McpReview> reviews = mcpReviewRepository.findByMcp(mcp, pageable);

		// 2. Page<McpReview> -> Page<McpReviewReadModel> 변환
		return reviews.map(review -> {
			String userName = getUserName(review.getUserId());
			return McpReviewReadModel.builder()
			                         .id(review.getId())
			                         .userSeq(review.getUserId())
			                         .userName(userName)
			                         .rating(review.getRating())
			                         .comment(review.getContent())
			                         .createdAt(review.getCreatedAt())
			                         .updatedAt(review.getUpdatedAt())
			                         .build();
		});
	}

	@Override
	@Transactional
	public Long saveReview(Long userId, Long mcpId, McpReviewRequest request) {
		Mcp mcp = mcpRepository.findByIdAndDeletedAtIsNull(mcpId)
		                       .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));
		McpReview mcpReview = McpReview.builder()
		                               .mcp(mcp)
		                               .userId(userId)
		                               .rating(request.getRating())
		                               .build();
		return mcpReviewRepository.save(mcpReview).getId();
	}

	@Override
	@Transactional
	public Long updateReview(Long userId, Long reviewId, McpReviewRequest request) {
		McpReview mcpReview = mcpReviewRepository.findByIdAndDeletedAtIsNull(reviewId)
		                                         .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

		if (!mcpReview.getUserId().equals(userId)) {
			throw new RestApiException(GlobalErrorStatus._VALIDATION_ERROR);
		}
		mcpReview.setRating(request.getRating());
		mcpReview.setContent(request.getComment());
		return mcpReviewRepository.save(mcpReview).getId();
	}

	@Override
	@Transactional
	public Long deleteReview(Long userId, Long reviewId) {
		McpReview mcpReview = mcpReviewRepository.findByIdAndDeletedAtIsNull(reviewId)
		                                         .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

		if (!mcpReview.getUserId().equals(userId)) {
			throw new RestApiException(GlobalErrorStatus._VALIDATION_ERROR);
		}
		mcpReviewRepository.delete(mcpReview);
		return reviewId;
	}

	private String getUserName(Long userId) {

		log.info("---- redis 조회 -----");
		Object cachedValue = redisTemplate.opsForValue().get(userId.toString());
		if (cachedValue != null) {
			return cachedValue.toString();
		}

		// 2. Redis에 없으면 gRPC 호출
		try {
			log.info("---- 캐시에 유저 이름 없음 -----");
			String userName = memberGrpcClient.getUserName(userId);

			// 3. Redis에 캐싱 (테스트를 위해 10초로 임시 저장)
			redisTemplate.opsForValue().set(userId.toString(), userName, 10, TimeUnit.SECONDS);

			return userName;
		} catch (Exception e) {
			log.error("유저 정보 조회 실패 userId={}", userId, e);
			return "알 수 없음";
		}
	}
}
