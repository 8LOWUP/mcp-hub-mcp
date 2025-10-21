package com.mcphub.domain.mcp.entity;

import com.mcphub.global.common.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "mcp_metrics")
public class McpMetrics extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mcp_id", nullable = false, unique = true)
	private Mcp mcp;

	private Integer reviewCount = 0;
	private Double avgRating = 0.0;
	private Integer savedUserCount = 0;
	private Double reviewScoreSum = 0.0;

	public void addSavedCount() {
		this.savedUserCount++;
	}

	public void removeSavedCount() {
		if (this.savedUserCount > 0) {
			this.savedUserCount--;
		}
	}

	// 리뷰 등록
	public void addReview(Double rating) {
		this.reviewCount++;
		this.reviewScoreSum += rating;
		this.avgRating = (double)this.reviewScoreSum / this.reviewCount;
	}

	// 리뷰 삭제
	public void removeReview(Double rating) {
		if (this.reviewCount > 0) {
			this.reviewCount--;
			this.reviewScoreSum -= rating;
			this.avgRating = this.reviewCount == 0 ? 0.0 :
				(double)this.reviewScoreSum / this.reviewCount;
		}
	}

	//리뷰 업데이트
	public void updateReview(Double oldRating, Double newRating) {
		this.reviewScoreSum = this.reviewScoreSum - oldRating + newRating;
		this.avgRating = this.reviewCount == 0 ? 0.0 :
			(double)this.reviewScoreSum / this.reviewCount;
	}
}
