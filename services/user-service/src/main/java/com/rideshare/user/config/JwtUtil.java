package com.rideshare.user.config;

import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JWTService {
    private final String SECRET_KEY = "secret";
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 1 day in milliseconds

    public String generateToken(String userId, String role) {
        return jwts.builder()
                .setSubject(userId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
}
