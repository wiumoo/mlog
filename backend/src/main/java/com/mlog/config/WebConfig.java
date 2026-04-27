package com.mlog.config;

import com.mlog.filter.JwtAuthFilter;
import com.mlog.utils.JwtUtils;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilter(JwtUtils jwtUtils) {

        FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<>();

        // Create JwtAuthFilter manually and inject JwtUtils
        registration.setFilter(new JwtAuthFilter(jwtUtils));

        // Apply filter to all URLs
        registration.addUrlPatterns("/*");

        // Set filter order
        registration.setOrder(1);

        return registration;
    }
}
