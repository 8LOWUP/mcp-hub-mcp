package com.mcphub.domain.mcp.repository.elasticsearch;

import com.mcphub.domain.mcp.entity.McpVector;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface McpIndexElasticsearchRepository extends ElasticsearchRepository<McpVector, String> {

}
