package com.example.booker.builder;

import com.example.booker.model.request.AuthRequest;

public final class AuthRequestBuilder {
    private AuthRequestBuilder() { }

    public static AuthRequest valid() {
        return AuthRequest.builder().username("admin").password("password123").build();
    }

    public static AuthRequest invalidPassword() {
        return AuthRequest.builder().username("admin").password("wrong-password").build();
    }

    public static AuthRequest missingCredentials() {
        return AuthRequest.builder().build();
    }
}
