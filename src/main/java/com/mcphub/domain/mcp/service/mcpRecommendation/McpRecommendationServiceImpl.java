package com.mcphub.domain.mcp.service.mcpRecommendation;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.mcphub.domain.mcp.entity.McpVector;
import com.mcphub.domain.mcp.repository.elasticsearch.McpElasticsearchRepository;
import com.mcphub.global.common.exception.RestApiException;
import com.mcphub.global.common.exception.code.status.GlobalErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class McpRecommendationServiceImpl implements McpRecommendationService {
    private final ElasticsearchClient elasticsearchClient;
    private final McpElasticsearchRepository repository;

    public List<McpVector> searchByVector(float[] queryVector, int k) {
        try {
            // 1. SearchRequest 생성
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("mcp_index")
                    .size(k)
                    .knn(knn -> knn
                            .field("embedding")
                            .queryVector(toFloatList(queryVector))
                            .k((long) k)
                            .numCandidates(100L)
                    )
            );

            // 2. Map으로 검색 결과 받기
            SearchResponse<Map> response = elasticsearchClient.search(searchRequest, Map.class);

            List<McpVector> results = new ArrayList<>();

            // 3. Map -> McpVector 변환
            for (Hit<Map> hit : response.hits().hits()) {
                Map source = hit.source();
                if (source != null) {
                    // embedding 변환
                    List<Number> embeddingList = (List<Number>) source.get("embedding");
                    float[] embedding = new float[embeddingList.size()];
                    for (int i = 0; i < embeddingList.size(); i++) {
                        embedding[i] = embeddingList.get(i).floatValue();
                    }

                    // McpVector 객체 생성
                    McpVector mcp = new McpVector(
                            ((Number) source.get("mcpId")).longValue(),
                            (String) source.get("name"),
                            (String) source.get("description"),
                            embedding
                    );

                    results.add(mcp);
                }
            }

            return results;

        } catch (Exception e) {
            throw new RestApiException(GlobalErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public McpVector processAndSaveDocument(Long mcpId, String title, String description, float[] embedding) {
        McpVector mcpVector = new McpVector(mcpId, title, description, embedding);
        return repository.save(mcpVector);
    }

    private List<Float> toFloatList(float[] vector) {
        List<Float> list = new ArrayList<>(vector.length);
        for (float v : vector) {
            list.add(v);
        }
        return list;
    }
}
