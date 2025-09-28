package com.rideshare.user.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    private final String SECRET_KEY = "mysecretkey123456mysecretkey123456mysecretkey123456"; // use env var in production
    private final long EXPIRATION_MS = 1000 * 60 * 30; // 30 minutes
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String userId, String role) {
        long startTime = System.currentTimeMillis();
        log.debug("Generating JWT token - UserId: {}, Role: {}", userId, role);
        
        try {
            String token = Jwts.builder()
                    .setSubject(userId)
                    .claim("role", role)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("JWT token generated successfully - UserId: {}, Duration: {}ms", userId, duration);
            
            return token;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Failed to generate JWT token - UserId: {}, Duration: {}ms, Error: {}", 
                    userId, duration, e.getMessage(), e);
            throw e;
        }
    }

    public Claims validateToken(String token) {
        long startTime = System.currentTimeMillis();
        log.debug("Validating JWT token");
        
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("JWT token validation successful - Subject: {}, Role: {}, Duration: {}ms", 
                    claims.getSubject(), claims.get("role"), duration);
            
            return claims;
        } catch (JwtException e) {
            long duration = System.currentTimeMillis() - startTime;
            log.warn("JWT token validation failed - Duration: {}ms, Error: {}", 
                    duration, e.getMessage());
            return null; // invalid token
        }
    }
}
