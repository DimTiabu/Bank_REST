package com.example.bankcards.exception;

public class CardIsNotActiveException extends RuntimeException {
    public CardIsNotActiveException() {
        super("Both cards must be active.");
    }
}
