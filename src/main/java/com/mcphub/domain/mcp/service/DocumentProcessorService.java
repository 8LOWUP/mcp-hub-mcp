package com.mcphub.domain.mcp.service;

import com.mcphub.domain.mcp.entity.Mcp;
import com.mcphub.domain.mcp.entity.McpVector;
import com.mcphub.domain.mcp.repository.elasticsearch.McpElasticsearchRepository;
import com.mcphub.domain.mcp.service.EmbeddingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 문서를 받아서 임베딩하고 Elasticsearch에 저장하는 서비스
 */
@Service
@AllArgsConstructor
public class DocumentProcessorService {
    private final McpElasticsearchRepository repository;

    @Transactional
    public McpVector processAndSaveDocument(Long mcpId, String title, String description, float[] embedding) {
        McpVector mcpVector = new McpVector(mcpId, title, description, embedding);
        return repository.save(mcpVector);
    }
}
