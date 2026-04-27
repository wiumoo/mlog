package com.mlog.config;

import com.mlog.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // Register LoginInterceptor
        registry.addInterceptor(loginInterceptor)
                // Apply to all paths
                .addPathPatterns("/**")
                // Exclude paths that do not require login
                .excludePathPatterns(
                        "/user/code",
                        "/user/login",
                        "/error"
                );
    }
}