package com.mcphub.domain.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Value("${spring.file.upload-dir}")
	private String staticLocations;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// /images/** 요청 시 -> file:/opt/mcphub/images/ 내부 파일 반환
		registry.addResourceHandler("/mcps/images/**")
		        .addResourceLocations(staticLocations)
		        .setCachePeriod(3600);
	}
}
