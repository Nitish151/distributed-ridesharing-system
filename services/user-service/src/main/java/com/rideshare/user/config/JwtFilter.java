package com.rideshare.user.config;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        long startTime = System.currentTimeMillis();
        
        log.debug("Processing request - Method: {}, URI: {}", method, requestUri);

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            log.debug("Authorization header found - URI: {}", requestUri);
            String token = authHeader.substring(7);
            
            Claims claims = jwtUtil.validateToken(token);
            if (claims != null) {
                String userId = claims.getSubject();
                String role = (String) claims.get("role");
                
                log.debug("JWT token validated successfully - URI: {}, UserId: {}, Role: {}", 
                        requestUri, userId, role);
                
                // Set user info in request attributes
                request.setAttribute("userId", userId);
                request.setAttribute("role", role);
                
                // Set Spring Security context
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userId, 
                    null, 
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
                
                log.debug("Spring Security context set for user - UserId: {}, Role: ROLE_{}", userId, role);
            } else {
                log.warn("JWT token validation failed - URI: {}, Method: {}", requestUri, method);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } else {
            log.debug("No authorization header or invalid format - URI: {}", requestUri);
        }
        
        try {
            filterChain.doFilter(request, response);
            long duration = System.currentTimeMillis() - startTime;
            log.debug("Request processed successfully - Method: {}, URI: {}, Duration: {}ms, Status: {}", 
                    method, requestUri, duration, response.getStatus());
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Error processing request - Method: {}, URI: {}, Duration: {}ms, Error: {}", 
                    method, requestUri, duration, e.getMessage(), e);
            throw e;
        }
    }
}
