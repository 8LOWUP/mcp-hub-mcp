package com.mcphub.domain.mcp.service.mcpRecommendation;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.mcphub.domain.mcp.entity.McpVector;
import com.mcphub.domain.mcp.repository.elasticsearch.McpIndexElasticsearchRepository;
import com.mcphub.global.common.exception.RestApiException;
import com.mcphub.global.common.exception.code.status.GlobalErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class McpRecommendationServiceImpl implements McpRecommendationService {
	private final ElasticsearchClient elasticsearchClient;
	private final McpIndexElasticsearchRepository repository;

	public List<McpVector> searchByVector(float[] queryVector, int k) {
		log.info(">>>>>>>>>>>>> start searchByVector");
		try {
			// 1. SearchRequest 생성
			log.info("11111111111111111111");
			SearchRequest searchRequest = SearchRequest.of(s -> s
				.index("mcp_index")
				.size(k)
				.knn(knn -> knn
					.field("embedding")
					.queryVector(toFloatList(queryVector))
					.k((long)k)
					.numCandidates(100L)
				)
			);
			log.info("2222222222222222222");
			// 2. Map으로 검색 결과 받기
			SearchResponse<Map> response = elasticsearchClient.search(searchRequest, Map.class);
			log.info("33333333333333333333");
			List<McpVector> results = new ArrayList<>();
			log.info("4444444444444444444");
			// 3. Map -> McpVector 변환
            for (Hit<Map> hit : response.hits().hits()) {
                Map source = hit.source();
                if (source == null) {
                    // 로그 남기기
                    log.warn("Hit source is null, skipping...");
                    continue;
                }

                try {
                    Object embeddingObj = source.get("embedding");
                    if (!(embeddingObj instanceof List)) {
                        log.warn("Invalid embedding type: {}", embeddingObj);
                        continue;
                    }

                    List<?> rawList = (List<?>) embeddingObj;
                    float[] embedding = new float[rawList.size()];
                    boolean embeddingValid = true;

                    for (int i = 0; i < rawList.size(); i++) {
                        Object value = rawList.get(i);
                        if (!(value instanceof Number)) {
                            log.warn("Invalid embedding value at index {}: {}", i, value);
                            embeddingValid = false;
                            break;
                        }
                        embedding[i] = ((Number) value).floatValue();
                    }

                    if (!embeddingValid) {
                        log.warn("Embedding skipped due to invalid values: {}", embeddingObj);
                        continue;
                    }

                    Object mcpIdObj = source.get("mcpId");
                    if (!(mcpIdObj instanceof Number)) {
                        log.warn("Invalid mcpId type: {}", mcpIdObj);
                        continue;
                    }
                    long mcpId = ((Number) mcpIdObj).longValue();

                    String name = source.get("name") instanceof String ? (String) source.get("name") : "";
                    String description = source.get("description") instanceof String ? (String) source.get("description") : "";

                    McpVector mcp = new McpVector(mcpId, name, description, embedding);
                    results.add(mcp);

                } catch (ClassCastException e) {
                    log.error("Type casting error while processing source: {}", source, e);
                } catch (NumberFormatException e) {
                    log.error("Number format error while processing source: {}", source, e);
                } catch (Exception e) {
                    log.error("Unexpected error while processing source: {}", source, e);
                }
            }
			log.info("55555555555555555555");
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
