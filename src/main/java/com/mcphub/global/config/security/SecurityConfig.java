package com.mcphub.global.config.security;

import com.mcphub.global.ratelimit.RateLimitFilter;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.mcphub.global.config.security.auth.CustomAccessDeniedHandler;
import com.mcphub.global.config.security.jwt.JwtAuthenticationFilter;
import com.mcphub.global.config.security.jwt.JwtExceptionFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import com.mcphub.global.config.security.jwt.JwtProvider;

import java.time.LocalDateTime;

import static com.mcphub.global.common.exception.code.status.AuthErrorStatus.INVALID_ACCESS_TOKEN;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    private final RateLimitFilter rateLimitFilter;
/*
    private final PrincipalDetailsService principalDetailsService;
*/


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configure(http))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/webjars/**").permitAll()
                        .requestMatchers("/s3/**").permitAll()
                        .requestMatchers("/auth/social/**").permitAll()
                        .requestMatchers( "/auth/token").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.accessDeniedHandler(customAccessDeniedHandler)
                                .authenticationEntryPoint(authenticationEntryPoint()))
                .addFilterBefore(new JwtExceptionFilter(), LogoutFilter.class) // filter 등록시 등록되어있는 필터와 순서를 정의해야함
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(rateLimitFilter, UsernamePasswordAuthenticationFilter.class) // RateLimitFilter를 JwtAuthenticationFilter 뒤에 추가
                .build();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(INVALID_ACCESS_TOKEN.getHttpStatus().value()); // 401
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(String.format(
                    "{\"timestamp\": \"%s\", \"code\": \"%s\", \"message\": \"%s\"}",
                    LocalDateTime.now(),
                    INVALID_ACCESS_TOKEN.getCode().getCode(),
                    INVALID_ACCESS_TOKEN.getMessage()
            ));
        };
    }
}
