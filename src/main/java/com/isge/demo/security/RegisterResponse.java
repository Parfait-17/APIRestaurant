package com.isge.demo.security;

public class RegisterResponse {
    private String message;

    public RegisterResponse(String message) {
        this.message = message;
    }

    // Getter
    public String getMessage() {
        return message;
    }
}