package com.rideshare.user.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String userId;
    private String email;
    private String role;
}