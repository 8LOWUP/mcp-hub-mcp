package com.mcphub.domain.mcp.service.mcpRecommendation;

import com.mcphub.domain.mcp.entity.McpVector;

import java.util.List;

public interface McpRecommendationService {
    McpVector processAndSaveDocument(Long mcpId, String title, String description, float[] embedding);
    List<McpVector> searchByVector(float[] queryVector, int k);
}
