package com.mlog.filter;

import com.mlog.utils.JwtUtils;
import com.mlog.utils.UserHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public JwtAuthFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String path = request.getRequestURI();

            // Exclude login-related APIs
            if (path.contains("/login") || path.contains("/sendCode")) {
                filterChain.doFilter(request, response);
                return;
            }

            String authorization = request.getHeader("Authorization");

            if (authorization == null || !authorization.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = authorization.substring(7);

            Long userId = jwtUtils.getUserIdFromToken(token);

            if (userId != null) {
                UserHolder.saveUser(userId);
            }

            filterChain.doFilter(request, response);

        } finally {
            UserHolder.removeUser();
        }
    }
}
