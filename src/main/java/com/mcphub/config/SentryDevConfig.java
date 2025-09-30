package com.mcphub.config;

import io.sentry.SentryOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class SentryDevConfig {

    @Bean
    public io.sentry.SentryOptions.BeforeSendCallback beforeSendCallback() {
        return (event, hint) -> {
            event.setEnvironment("workspace-service");
            return event;
        };
    }
}
