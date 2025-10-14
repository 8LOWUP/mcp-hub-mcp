package com.mcphub.domain.mcp.repository.querydsl.impl;

import com.mcphub.domain.mcp.dto.request.McpListRequest;
import com.mcphub.domain.mcp.dto.response.api.McpToolResponse;
import com.mcphub.domain.mcp.dto.response.readmodel.McpReadModel;
import com.mcphub.domain.mcp.dto.response.readmodel.MyUploadMcpDetailReadModel;
import com.mcphub.domain.mcp.entity.QArticleMcpTool;
import com.mcphub.domain.mcp.entity.QCategory;
import com.mcphub.domain.mcp.entity.QLicense;
import com.mcphub.domain.mcp.entity.QMcpReview;
import com.mcphub.domain.mcp.entity.QPlatform;
import com.mcphub.domain.mcp.entity.QUserMcp;
import com.mcphub.domain.mcp.repository.querydsl.McpDslRepository;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import com.mcphub.domain.mcp.entity.Mcp;
import com.querydsl.core.BooleanBuilder;
import com.mcphub.domain.mcp.entity.QMcp;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class McpDslRepositoryImpl implements McpDslRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<McpReadModel> searchMcps(McpListRequest req, Pageable pageable) {
		QMcp mcp = QMcp.mcp;
		QUserMcp userMcp = QUserMcp.userMcp;
		QMcpReview review = QMcpReview.mcpReview;

		BooleanBuilder builder = new BooleanBuilder();
		builder.and(mcp.isPublished.eq(true).and(mcp.deletedAt.isNull()));
		// 검색 조건
		if (req.getSearch() != null && !req.getSearch().isBlank()) {
			builder.and(mcp.name.containsIgnoreCase(req.getSearch())
			                    .or(mcp.description.containsIgnoreCase(req.getSearch())));
		}

		// 카테고리 조건
		if (req.getCategory() != null) {
			builder.and(mcp.category.id.eq(req.getCategory()));
		}

		// 정렬 조건
		OrderSpecifier<?> orderSpecifier;
		if ("popular".equalsIgnoreCase(req.getSort())) {
			orderSpecifier = userMcp.count().desc();
		} else if ("rating".equalsIgnoreCase(req.getSort())) {
			orderSpecifier = review.rating.avg().desc();
		} else {
			orderSpecifier = mcp.createdAt.desc(); // 기본 최신순
		}

		// 실제 조회
		List<McpReadModel> content = queryFactory
			.select(Projections.bean(McpReadModel.class,
				mcp.id,
				mcp.name,
				mcp.version,
				mcp.description,
				mcp.imageUrl,
				mcp.sourceUrl,
				mcp.isKeyRequired,
				mcp.category.id.as("categoryId"),
				mcp.category.name.as("categoryName"),
				mcp.platform.id.as("platformId"),
				mcp.platform.name.as("platformName"),
				mcp.license.id.as("licenseId"),
				mcp.license.name.as("licenseName"),
				mcp.developerName.as("developerName"),
				review.rating.avg().as("averageRating"),
				userMcp.count().as("savedUserCount"),
				mcp.isPublished,
				mcp.publishedAt,
				mcp.createdAt,
				mcp.updatedAt
			))
			.from(mcp)
			.leftJoin(mcp.category)
			.leftJoin(mcp.platform)
			.leftJoin(mcp.license)
			.leftJoin(userMcp).on(userMcp.mcp.eq(mcp))
			.leftJoin(review).on(review.mcp.eq(mcp))
			.where(builder)
			.groupBy(mcp.id)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(orderSpecifier)
			.fetch();

		// 전체 카운트
		long total = queryFactory
			.select(mcp.count())
			.from(mcp)
			.where(builder)
			.fetchOne();

		return new PageImpl<>(content, pageable, total);
	}

	@Override
	public McpReadModel getMcpDetail(Long id) {
		QMcp mcp = QMcp.mcp;
		QCategory category = QCategory.category;
		QPlatform platform = QPlatform.platform;
		QLicense license = QLicense.license;
		QUserMcp userMcp = QUserMcp.userMcp;

		return queryFactory
			.select(Projections.bean(
				McpReadModel.class,
				mcp.id,
				mcp.name,
				mcp.version,
				mcp.description,
				mcp.imageUrl,
				mcp.sourceUrl,
                mcp.requestUrl,
				mcp.isKeyRequired,
				mcp.developerName,
                mcp.publishedAt,
				category.id.as("categoryId"),
				category.name.as("categoryName"),
				platform.id.as("platformId"),
				platform.name.as("platformName"),
				license.id.as("licenseId"),
				license.name.as("licenseName"),
				ExpressionUtils.as(
					JPAExpressions
						.select(userMcp.count())
						.from(userMcp)
						.where(userMcp.mcp.id.eq(id)),
					"savedUserCount"
				)
			))
			.from(mcp)
			.leftJoin(mcp.category, category)
			.leftJoin(mcp.platform, platform)
			.leftJoin(mcp.license, license)
			.where(mcp.id.eq(id).and(mcp.isPublished.eq(true)).and(mcp.deletedAt.isNull()))
			.fetchOne();
	}

	@Override
	public Page<McpReadModel> searchMyUploadMcps(McpListRequest req, Pageable pageable, Long userId) {
		QMcp mcp = QMcp.mcp;
		QUserMcp userMcp = QUserMcp.userMcp;
		QMcpReview review = QMcpReview.mcpReview;

		BooleanBuilder builder = new BooleanBuilder();

		builder.and(mcp.userId.eq(userId)).and(mcp.deletedAt.isNull());

		// 검색 조건
		if (req.getSearch() != null && !req.getSearch().isBlank()) {
			builder.and(mcp.name.containsIgnoreCase(req.getSearch())
			                    .or(mcp.description.containsIgnoreCase(req.getSearch())));
		}

		// 카테고리 조건
		if (req.getCategory() != null) {
			builder.and(mcp.category.id.eq(req.getCategory()));
		}

		// 정렬 조건
		OrderSpecifier<?> orderSpecifier;
		if ("popular".equalsIgnoreCase(req.getSort())) {
			orderSpecifier = userMcp.count().desc();
		} else if ("rating".equalsIgnoreCase(req.getSort())) {
			orderSpecifier = review.rating.avg().desc();
		} else {
			orderSpecifier = mcp.createdAt.desc(); // 기본 최신순
		}

		// 조회
		List<McpReadModel> content = queryFactory
			.select(Projections.bean(McpReadModel.class,
				mcp.id,
				mcp.name,
				mcp.version,
				mcp.description,
				mcp.imageUrl,
				mcp.sourceUrl,
				mcp.isKeyRequired,
				mcp.category.id.as("categoryId"),
				mcp.category.name.as("categoryName"),
				mcp.platform.id.as("platformId"),
				mcp.platform.name.as("platformName"),
				mcp.license.id.as("licenseId"),
				mcp.license.name.as("licenseName"),
				review.rating.avg().as("averageRating"),
				userMcp.count().as("savedUserCount"),
				mcp.isPublished,
				mcp.publishedAt,
				mcp.lastPublishedAt
			))
			.from(mcp)
			.leftJoin(mcp.category)
			.leftJoin(mcp.platform)
			.leftJoin(mcp.license)
			.leftJoin(userMcp).on(userMcp.mcp.eq(mcp))
			.leftJoin(review).on(review.mcp.eq(mcp))
			.where(builder)
			.groupBy(mcp.id)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(orderSpecifier)
			.fetch();

		// 전체 카운트
		long total = queryFactory
			.select(mcp.count())
			.from(mcp)
			.where(builder)
			.fetchOne();

		return new PageImpl<>(content, pageable, total);

	}

	@Override
	public MyUploadMcpDetailReadModel getMyUploadMcpDetail(Long mcpId) {
		QMcp mcp = QMcp.mcp;
		QCategory category = QCategory.category;
		QPlatform platform = QPlatform.platform;
		QLicense license = QLicense.license;
		QMcpReview review = QMcpReview.mcpReview;
		QUserMcp userMcp = QUserMcp.userMcp;

		return queryFactory
			.select(Projections.fields(
				MyUploadMcpDetailReadModel.class,
				mcp.id,
				mcp.name,
				mcp.version,
				mcp.description,
				mcp.imageUrl,
				mcp.sourceUrl,
				mcp.requestUrl,
				mcp.developerName,
				mcp.isKeyRequired,
				category.id.as("categoryId"),
				category.name.as("categoryName"),
				platform.id.as("platformId"),
				platform.name.as("platformName"),
				license.id.as("licenseId"),
				license.name.as("licenseName"),
				review.rating.avg().as("averageRating"),
				userMcp.count().as("savedUserCount"),
				mcp.isPublished.as("isPublished"),
				mcp.publishedAt,
				mcp.lastPublishedAt.as("lastPublishedAt")
			))
			.from(mcp)
			.leftJoin(mcp.category, category)
			.leftJoin(mcp.platform, platform)
			.leftJoin(mcp.license, license)
			.leftJoin(review).on(review.mcp.eq(mcp))
			.leftJoin(userMcp).on(userMcp.mcp.eq(mcp))
			.where(mcp.id.eq(mcpId).and(mcp.deletedAt.isNull()))
			.groupBy(
				mcp.id,
				category.id,
				category.name,
				platform.id,
				platform.name,
				license.id,
				license.name,
				mcp.isPublished,
				mcp.lastPublishedAt,
				mcp.createdAt,
				mcp.updatedAt
			)
			.fetchOne();
	}

	@Override
	public List<McpToolResponse> getMcpTools(Long mcpId) {
		QArticleMcpTool mcpTool = QArticleMcpTool.articleMcpTool;
		List<McpToolResponse> tools = queryFactory
			.select(Projections.bean(McpToolResponse.class,
				mcpTool.id.as("id"),
				mcpTool.name.as("name"),
				mcpTool.content.as("content")
			))
			.from(mcpTool)
			.where(mcpTool.mcp.id.eq(mcpId))
			.fetch();

		return tools;
	}
}
