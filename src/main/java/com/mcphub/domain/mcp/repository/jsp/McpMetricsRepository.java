package com.mcphub.domain.mcp.repository.jsp;

import com.mcphub.domain.mcp.entity.Mcp;
import com.mcphub.domain.mcp.entity.McpMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface McpMetricsRepository extends JpaRepository<McpMetrics, Long> {
	Optional<McpMetrics> findByMcp(Mcp mcp);

	Optional<McpMetrics> findByMcp_Id(Long mcpId);
}
