package com.example.bankcards.exception;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException() {
        super("Authentication error");
    }
}
