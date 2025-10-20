package com.mcphub.domain.mcp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
