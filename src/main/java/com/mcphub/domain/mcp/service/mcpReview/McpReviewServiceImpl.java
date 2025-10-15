package com.mcphub.domain.mcp.service.mcpReview;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcphub.domain.mcp.error.McpErrorStatus;
import com.mcphub.domain.mcp.dto.request.McpReviewListRequest;
import com.mcphub.domain.mcp.dto.request.McpReviewRequest;
import com.mcphub.domain.mcp.dto.response.readmodel.McpReviewReadModel;
import com.mcphub.domain.mcp.entity.Mcp;
import com.mcphub.domain.mcp.entity.McpReview;
import com.mcphub.domain.mcp.grpc.MemberGrpcClient;
import com.mcphub.domain.mcp.repository.jsp.McpRepository;
import com.mcphub.domain.mcp.repository.jsp.McpReviewRepository;
import com.mcphub.global.common.exception.RestApiException;
import com.mcphub.global.common.exception.code.status.GlobalErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class McpReviewServiceImpl implements McpReviewService {
	private final McpReviewRepository mcpReviewRepository;
	private final RedisTemplate<String, String> redisTemplate;
	private final McpRepository mcpRepository;
	private final MemberGrpcClient memberGrpcClient;
	private final ObjectMapper objectMapper;

	@Override
	@Transactional(readOnly = true)
	public Page<McpReviewReadModel> getMcpReviewList(Pageable pageable, McpReviewListRequest request, Long mcpId) {
		Mcp mcp = mcpRepository.findByIdAndDeletedAtIsNull(mcpId)
		                       .orElseThrow(() -> new RestApiException(McpErrorStatus._NOT_FOUND));
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
		                       .orElseThrow(() -> new RestApiException(McpErrorStatus._NOT_FOUND));
		McpReview mcpReview = McpReview.builder()
		                               .mcp(mcp)
		                               .userId(userId)
		                               .rating(request.getRating())
		                               .content(request.getComment())
		                               .build();
		return mcpReviewRepository.save(mcpReview).getId();
	}

	@Override
	@Transactional
	public Long updateReview(Long userId, Long reviewId, McpReviewRequest request) {
		McpReview mcpReview = mcpReviewRepository.findByIdAndDeletedAtIsNull(reviewId)
		                                         .orElseThrow(() -> new RestApiException(McpErrorStatus._NOT_FOUND));

		if (!mcpReview.getUserId().equals(userId)) {
			throw new RestApiException(McpErrorStatus._FORBIDDEN);
		}
		mcpReview.setRating(request.getRating());
		mcpReview.setContent(request.getComment());
		return mcpReviewRepository.save(mcpReview).getId();
	}

	@Override
	@Transactional
	public Long deleteReview(Long userId, Long reviewId) {
		McpReview mcpReview = mcpReviewRepository.findByIdAndDeletedAtIsNull(reviewId)
		                                         .orElseThrow(() -> new RestApiException(McpErrorStatus._NOT_FOUND));

		if (!mcpReview.getUserId().equals(userId)) {
			throw new RestApiException(McpErrorStatus._FORBIDDEN);
		}
		mcpReviewRepository.delete(mcpReview);
		return reviewId;
	}

	private String getUserName(Long userId) {

		log.info("---- redis 조회 -----");
		Object cachedValue = redisTemplate.opsForValue().get("cached_member:" + userId.toString());
		if (cachedValue != null) {
			try {
				// cachedValue는 JSON 문자열이므로 JsonNode로 파싱
				JsonNode node = objectMapper.readTree(cachedValue.toString());
				String nickname = node.get("nickname").asText();
				log.info("Redis 캐시에서 nickname 조회됨: {}", nickname);
				return nickname;
			} catch (Exception e) {
				log.error("Redis 캐시 역직렬화 실패", e);
				throw new RestApiException(GlobalErrorStatus._INTERNAL_SERVER_ERROR);
			}
		}

		// 2. Redis에 없으면 gRPC 호출
		try {
			log.info("---- 캐시에 유저 이름 없음 -----");
			String userName = memberGrpcClient.getUserName(userId);

			// 3. Redis에 캐싱 (테스트를 위해 10초로 임시 저장)
			Map<String, Object> data = new HashMap<>();

			// CDC 포맷 유지하면서 nickname만 실제값으로 채움
			data.put("id", userId);
			data.put("nickname", userName);

			// JSON 문자열로 직렬화
			String jsonValue = objectMapper.writeValueAsString(data);

			// Redis에 저장 (TTL 10초 유지)
			redisTemplate.opsForValue()
			             .set("cached_member:" + userId, jsonValue, 10, TimeUnit.SECONDS);

			return userName;
		} catch (Exception e) {
			log.error("유저 정보 조회 실패 userId={}", userId, e);
			throw new RestApiException(McpErrorStatus._USER_NOT_FOUND);
		}
	}
}
