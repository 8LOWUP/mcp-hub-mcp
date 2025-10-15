package com.mcphub.domain.mcp.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@AllArgsConstructor
@Getter
@Document(indexName = "mcp_index")
public class McpVector {
    @Id
    private Long mcpId;

    private String name;
    private String description;

    @Field(type = FieldType.Dense_Vector, dims = 1536)
    private float[] embedding; // dense_vector 저장
}