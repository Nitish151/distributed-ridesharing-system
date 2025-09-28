package com.rideshare.user.service;

import com.rideshare.user.model.User;
import com.rideshare.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        long startTime = System.currentTimeMillis();
        log.debug("Loading user details for authentication - Email: {}", email);
        
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.warn("Authentication failed - User not found: {}", email);
                        return new UsernameNotFoundException("User not found");
                    });

            log.debug("User found for authentication - UserId: {}, Role: {}", user.getId(), user.getRole());
            
            GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
            log.debug("Granted authority for user {}: {}", email, authority.getAuthority());

            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    Collections.singleton(authority)
            );
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("User details loaded successfully for authentication - Email: {}, Duration: {}ms", 
                    email, duration);
            
            return userDetails;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Failed to load user details for authentication - Email: {}, Duration: {}ms, Error: {}", 
                    email, duration, e.getMessage());
            throw e;
        }
    }
}
