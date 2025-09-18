package com.mcphub.global.config.ratelimit;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Setter
@Getter
@ConfigurationProperties(prefix = "ratelimit")
public class RateLimitProperties {
    private boolean enabled = true;
    private int limit = 10;
    private Duration period = Duration.ofSeconds(1);
    private List<String> whitelistPaths = List.of("/actuator/**", "/swagger-ui/**", "/v3/api-docs/**");

}
