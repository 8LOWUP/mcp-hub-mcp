package com.mcphub.domain.mcp.repository.jsp;

import com.mcphub.domain.mcp.entity.Mcp;
import com.mcphub.domain.mcp.entity.McpReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface McpReviewRepository extends JpaRepository<McpReview, Long> {

	Optional<McpReview> findByIdAndDeletedAtIsNull(Long id);

	Page<McpReview> findByMcp(Mcp mcp, Pageable pageable);

}
