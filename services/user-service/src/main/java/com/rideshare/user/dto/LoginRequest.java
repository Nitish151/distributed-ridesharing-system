package com.rideshare.user.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;      // or phone if you want to allow login via phone
    private String password;
}
