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

            // 로그인 관련 API는 JWT 검증 없이 통과
            if (path.equals("/user/login") || path.equals("/user/code") || path.equals("/error")) {
                filterChain.doFilter(request, response);
                return;
            }

            // CORS preflight 요청 통과
            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
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
