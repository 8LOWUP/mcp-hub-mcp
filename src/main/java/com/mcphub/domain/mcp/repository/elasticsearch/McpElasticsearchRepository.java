package com.mcphub.domain.mcp.repository.elasticsearch;

import com.mcphub.domain.mcp.entity.McpElasticsearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface McpElasticsearchRepository extends ElasticsearchRepository<McpElasticsearch, String> {

}
