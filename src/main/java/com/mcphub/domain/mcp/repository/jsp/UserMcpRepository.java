package com.mcphub.domain.mcp.repository.jsp;

import com.mcphub.domain.mcp.entity.Mcp;
import com.mcphub.domain.mcp.entity.UserMcp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMcpRepository extends JpaRepository<UserMcp, Long> {
	@Query("SELECT COUNT(u) FROM UserMcp u WHERE u.mcp.id = :mcpId")
	Integer getSavedUserCount(@Param("mcpId") Long mcpId);

	boolean existsByUserIdAndMcpId(Long userId, Long mcpId);

	int deleteByUserIdAndMcp(Long userId, Mcp mcp);

	Mcp mcp(Mcp mcp);

	Page<UserMcp> findByUserId(Long userId, Pageable pageable);

	Optional<UserMcp> findByUserIdAndMcp(Long userId, Mcp mcp);

	@Query("""
        SELECT CASE WHEN COUNT(um) > 0 THEN TRUE ELSE FALSE END
        FROM UserMcp um
        WHERE um.userId = :userId
          AND um.platformId = :platformId
          AND um.platformToken IS NOT NULL
    """)
	boolean existsTokenRegistered(@Param("userId") Long userId, @Param("platformId") Long platformId);

	Optional<UserMcp> findFirstByUserIdAndPlatformId(Long userId, Long platformId);

	boolean existsByUserIdAndPlatformIdAndPlatformTokenIsNotNull(Long userId, Long platformId);

	@Query("SELECT DISTINCT um.platformId FROM UserMcp um WHERE um.userId = :userId")
	List<Long> findDistinctPlatformIdsByUserId(@Param("userId") Long userId);

}
