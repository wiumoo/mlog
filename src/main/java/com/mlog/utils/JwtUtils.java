package com.mlog.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expire}")
    private long expireTime;

    // Generate JWT token with userId and nickname
    public String generateToken(Long userId, String nickname) {

        // Convert secret string to SecretKey
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("nickname", nickname)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(key)
                .compact();
    }

    /**
     * Parse JWT token and extract userId from it
     *
     * @param token JWT token string (without "Bearer ")
     * @return userId stored inside the token, or null if invalid
     */
    public Long getUserIdFromToken(String token) {
        try {
            // Create signing key using secretKey
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

            // Parse the token and extract claims (payload)
            Claims claims = Jwts.parser()
                    .verifyWith(key)          // verify signature using secret key
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // "sub" (subject) contains userId
            String userIdStr = claims.getSubject();

            // Convert String → Long
            return Long.valueOf(userIdStr);

        } catch (Exception e) {
            // Invalid token: expired, tampered, malformed, or unsupported
            return null;
        }
    }
}
