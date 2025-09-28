package com.rideshare.user.service;

import com.rideshare.user.config.JwtUtil;
import com.rideshare.user.dto.*;
import com.rideshare.user.dto.LoginRequest;
import com.rideshare.user.dto.LoginResponse;
import com.rideshare.user.dto.RegisterRequest;
import com.rideshare.user.model.User;
import com.rideshare.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // will hash passwords
    private final JwtUtil jwtUtil;
    private final UserEventPublisher userEventPublisher;

    public User registerUser(RegisterRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Starting user registration process - Email: {}, Name: {}", 
                request.getEmail(), request.getName());
        
        try {
            // check if user already exists
            log.debug("Checking if user already exists with email: {}", request.getEmail());
            userRepository.findByEmail(request.getEmail())
                    .ifPresent(u -> {
                        log.warn("Registration attempt failed - Email already exists: {}", request.getEmail());
                        throw new RuntimeException("Email already registered!");
                    });

            log.debug("Email {} is available for registration", request.getEmail());            // create and save
            User user = User.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .password(passwordEncoder.encode(request.getPassword())) // hash
                    .role(User.Role.USER) // default role
                    .walletBalance(BigDecimal.valueOf(0.0))
                    .build();
    
            log.debug("Created user object for registration - Email: {}, Role: {}", 
                    user.getEmail(), user.getRole());
            
            User savedUser = userRepository.save(user);
            
            // Publish UserCreatedEvent
            String correlationId = java.util.UUID.randomUUID().toString();
            userEventPublisher.publishUserCreated(savedUser, correlationId);
            
            long duration = System.currentTimeMillis() - startTime;
            
            log.info("User registration completed successfully - UserId: {}, Email: {}, Duration: {}ms", 
                    savedUser.getId(), savedUser.getEmail(), duration);
            
            return savedUser;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("User registration failed - Email: {}, Duration: {}ms, Error: {}", 
                    request.getEmail(), duration, e.getMessage(), e);
            throw e;
        }
    }

    public LoginResponse login(LoginRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Starting user login process - Email: {}", request.getEmail());
        
        try {
            log.debug("Looking up user by email: {}", request.getEmail());
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> {
                        log.warn("Login attempt failed - User not found: {}", request.getEmail());
                        return new RuntimeException("User not found");
                    });
    
            log.debug("User found - UserId: {}, Role: {}", user.getId(), user.getRole());
            
            log.debug("Validating password for user: {}", request.getEmail());
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                log.warn("Login attempt failed - Invalid password for user: {}", request.getEmail());
                throw new RuntimeException("Invalid credentials");
            }
    
            log.debug("Password validation successful for user: {}", request.getEmail());
            log.debug("Generating JWT token for user: {} with role: {}", user.getId(), user.getRole());
            
            String token = jwtUtil.generateToken(user.getId().toString(), user.getRole().name());
    
            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setUserId(user.getId().toString());
            response.setEmail(user.getEmail());
            response.setRole(user.getRole().name());
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("User login completed successfully - UserId: {}, Email: {}, Role: {}, Duration: {}ms", 
                    user.getId(), user.getEmail(), user.getRole(), duration);
            
            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("User login failed - Email: {}, Duration: {}ms, Error: {}", 
                    request.getEmail(), duration, e.getMessage());
            throw e;
        }
    }

    public UserProfileResponse getUserProfile(String userId) {
        long startTime = System.currentTimeMillis();
        log.info("Getting user profile - UserId: {}", userId);
        
        try {
            User user = userRepository.findByIdAndNotDeleted(java.util.UUID.fromString(userId))
                    .orElseThrow(() -> {
                        log.warn("User profile not found - UserId: {}", userId);
                        return new RuntimeException("User not found");
                    });
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("User profile retrieved successfully - UserId: {}, Duration: {}ms", 
                    userId, duration);
            
            return UserProfileResponse.fromUser(user);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Failed to get user profile - UserId: {}, Duration: {}ms, Error: {}", 
                    userId, duration, e.getMessage(), e);
            throw e;
        }
    }

    public UserProfileResponse updateUserProfile(String userId, UpdateProfileRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Updating user profile - UserId: {}", userId);
        
        try {
            User user = userRepository.findByIdAndNotDeleted(java.util.UUID.fromString(userId))
                    .orElseThrow(() -> {
                        log.warn("User not found for profile update - UserId: {}", userId);
                        return new RuntimeException("User not found");
                    });
            
            // Update fields if provided
            if (request.getName() != null && !request.getName().trim().isEmpty()) {
                log.debug("Updating user name - UserId: {}", userId);
                user.setName(request.getName().trim());
            }
            
            if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
                log.debug("Updating user phone - UserId: {}", userId);
                // Check if phone is already taken by another user
                userRepository.findByPhone(request.getPhone())
                        .ifPresent(existingUser -> {
                            if (!existingUser.getId().equals(user.getId())) {
                                log.warn("Phone number already exists - UserId: {}, Phone: {}", 
                                        userId, request.getPhone());
                                throw new RuntimeException("Phone number already registered");
                            }
                        });
                user.setPhone(request.getPhone().trim());
            }
            
            User updatedUser = userRepository.save(user);
            
            // Publish UserUpdatedEvent
            String correlationId = java.util.UUID.randomUUID().toString();
            userEventPublisher.publishUserUpdated(updatedUser, user, correlationId);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("User profile updated successfully - UserId: {}, Duration: {}ms", 
                    userId, duration);
            
            return UserProfileResponse.fromUser(updatedUser);
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Failed to update user profile - UserId: {}, Duration: {}ms, Error: {}", 
                    userId, duration, e.getMessage(), e);
            throw e;
        }
    }

    public void changePassword(String userId, ChangePasswordRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Changing password for user - UserId: {}", userId);
        
        try {
            User user = userRepository.findByIdAndNotDeleted(java.util.UUID.fromString(userId))
                    .orElseThrow(() -> {
                        log.warn("User not found for password change - UserId: {}", userId);
                        return new RuntimeException("User not found");
                    });
            
            // Validate current password
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                log.warn("Invalid current password for password change - UserId: {}", userId);
                throw new RuntimeException("Current password is incorrect");
            }
            
            // Validate new password
            if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
                log.warn("Invalid new password for password change - UserId: {}", userId);
                throw new RuntimeException("New password must be at least 6 characters long");
            }
            
            // Update password
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("Password changed successfully - UserId: {}, Duration: {}ms", 
                    userId, duration);
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Failed to change password - UserId: {}, Duration: {}ms, Error: {}", 
                    userId, duration, e.getMessage(), e);
            throw e;
        }
    }

    public void deleteUser(String userId, String requestingUserId) {
        long startTime = System.currentTimeMillis();
        log.info("Deleting user - UserId: {}, RequestedBy: {}", userId, requestingUserId);
        
        try {
            User user = userRepository.findByIdAndNotDeleted(java.util.UUID.fromString(userId))
                    .orElseThrow(() -> {
                        log.warn("User not found for deletion - UserId: {}", userId);
                        return new RuntimeException("User not found");
                    });
            
            // Users can only delete themselves unless they're an admin
            User requestingUser = userRepository.findByIdAndNotDeleted(java.util.UUID.fromString(requestingUserId))
                    .orElseThrow(() -> {
                        log.warn("Requesting user not found - RequestingUserId: {}", requestingUserId);
                        return new RuntimeException("Requesting user not found");
                    });
            
            if (!userId.equals(requestingUserId) && requestingUser.getRole() != User.Role.ADMIN) {
                log.warn("Unauthorized user deletion attempt - UserId: {}, RequestedBy: {}", 
                        userId, requestingUserId);
                throw new RuntimeException("You can only delete your own account");
            }
            
            // Perform soft delete
            user.softDelete();
            userRepository.save(user);
            
            // Publish UserDeletedEvent
            String correlationId = java.util.UUID.randomUUID().toString();
            userEventPublisher.publishUserDeleted(user.getId(), user.getEmail(), correlationId);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("User soft deleted successfully - UserId: {}, RequestedBy: {}, Duration: {}ms", 
                    userId, requestingUserId, duration);
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Failed to delete user - UserId: {}, RequestedBy: {}, Duration: {}ms, Error: {}", 
                    userId, requestingUserId, duration, e.getMessage(), e);
            throw e;
        }
    }
    
}
