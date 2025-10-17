package com.mcphub.domain.mcp.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@Document(indexName = "mcp", createIndex = true)
public class McpElasticsearch {

	@Id
	@Field(type = FieldType.Long)
	private Long mcpId;

	@Field(type = FieldType.Text)
	private String title;

	@Field(type = FieldType.Text)
	private String content;
}
