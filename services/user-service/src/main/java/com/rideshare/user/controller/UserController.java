package com.rideshare.user.controller;

import com.rideshare.user.dto.LoginRequest;
import com.rideshare.user.dto.LoginResponse;
import com.rideshare.user.dto.RegisterRequest;
import com.rideshare.user.dto.UpdateProfileRequest;
import com.rideshare.user.dto.ChangePasswordRequest;
import com.rideshare.user.dto.UserProfileResponse;
import com.rideshare.user.model.User;
import com.rideshare.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.Map;
import org.slf4j.MDC;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/auth/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
        String correlationId = UUID.randomUUID().toString();
        String clientIp = getClientIpAddress(httpRequest);
        
        log.info("[{}] Registration request started - Email: {}, IP: {}", 
                correlationId, request.getEmail(), clientIp);
        
        try {
            User user = userService.registerUser(request);
            log.info("[{}] User registration successful - UserId: {}, Email: {}", 
                    correlationId, user.getId(), user.getEmail());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("[{}] User registration failed - Email: {}, Error: {}", 
                    correlationId, request.getEmail(), e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String correlationId = UUID.randomUUID().toString();
        String clientIp = getClientIpAddress(httpRequest);
        
        log.info("[{}] Login request started - Email: {}, IP: {}", 
                correlationId, request.getEmail(), clientIp);
        
        try {
            LoginResponse response = userService.login(request);
            log.info("[{}] User login successful - Email: {}, UserId: {}, Role: {}", 
                    correlationId, request.getEmail(), response.getUserId(), response.getRole());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.warn("[{}] User login failed - Email: {}, Error: {}", 
                    correlationId, request.getEmail(), e.getMessage());
            throw e;
        }
    }

    @GetMapping("/users/profile")
    public ResponseEntity<?> getUserProfile(HttpServletRequest request) {
        String correlationId = UUID.randomUUID().toString();
        String clientIp = getClientIpAddress(request);
        
        try {
            String userId = (String) request.getAttribute("userId");
            
            log.info("[{}] Get user profile request - UserId: {}, IP: {}", 
                    correlationId, userId, clientIp);
            
            if (userId == null) {
                log.warn("[{}] User profile request without authentication - IP: {}", 
                        correlationId, clientIp);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Authentication required"));
            }
            
            UserProfileResponse profile = userService.getUserProfile(userId);
            
            log.info("[{}] User profile retrieved successfully - UserId: {}, IP: {}", 
                    correlationId, userId, clientIp);
            
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            String userId = (String) request.getAttribute("userId");
            log.error("[{}] Get user profile failed - UserId: {}, IP: {}, Error: {}", 
                    correlationId, userId, clientIp, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/users/profile")
    public ResponseEntity<?> updateUserProfile(@RequestBody UpdateProfileRequest updateRequest, 
                                               HttpServletRequest request) {
        String correlationId = UUID.randomUUID().toString();
        String clientIp = getClientIpAddress(request);
        
        try {
            String userId = (String) request.getAttribute("userId");
            
            log.info("[{}] Update user profile request - UserId: {}, IP: {}", 
                    correlationId, userId, clientIp);
            
            if (userId == null) {
                log.warn("[{}] Update profile request without authentication - IP: {}", 
                        correlationId, clientIp);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Authentication required"));
            }
            
            UserProfileResponse updatedProfile = userService.updateUserProfile(userId, updateRequest);
            
            log.info("[{}] User profile updated successfully - UserId: {}, IP: {}", 
                    correlationId, userId, clientIp);
            
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            String userId = (String) request.getAttribute("userId");
            log.error("[{}] Update user profile failed - UserId: {}, IP: {}, Error: {}", 
                    correlationId, userId, clientIp, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/users/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, 
                                            HttpServletRequest request) {
        String correlationId = UUID.randomUUID().toString();
        String clientIp = getClientIpAddress(request);
        
        try {
            String userId = (String) request.getAttribute("userId");
            
            log.info("[{}] Change password request - UserId: {}, IP: {}", 
                    correlationId, userId, clientIp);
            
            if (userId == null) {
                log.warn("[{}] Change password request without authentication - IP: {}", 
                        correlationId, clientIp);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Authentication required"));
            }
            
            userService.changePassword(userId, changePasswordRequest);
            
            log.info("[{}] Password changed successfully - UserId: {}, IP: {}", 
                    correlationId, userId, clientIp);
            
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (Exception e) {
            String userId = (String) request.getAttribute("userId");
            log.error("[{}] Change password failed - UserId: {}, IP: {}, Error: {}", 
                    correlationId, userId, clientIp, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId, HttpServletRequest request) {
        String correlationId = UUID.randomUUID().toString();
        String clientIp = getClientIpAddress(request);
        
        try {
            String requestingUserId = (String) request.getAttribute("userId");
            
            log.info("[{}] Delete user request - TargetUserId: {}, RequestingUserId: {}, IP: {}", 
                    correlationId, userId, requestingUserId, clientIp);
            
            if (requestingUserId == null) {
                log.warn("[{}] Delete user request without authentication - TargetUserId: {}, IP: {}", 
                        correlationId, userId, clientIp);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Authentication required"));
            }
            
            userService.deleteUser(userId, requestingUserId);
            
            log.info("[{}] User deleted successfully - TargetUserId: {}, RequestingUserId: {}, IP: {}", 
                    correlationId, userId, requestingUserId, clientIp);
            
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            String requestingUserId = (String) request.getAttribute("userId");
            log.error("[{}] Delete user failed - TargetUserId: {}, RequestingUserId: {}, IP: {}, Error: {}", 
                    correlationId, userId, requestingUserId, clientIp, e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
