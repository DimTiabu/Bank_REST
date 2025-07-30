package com.example.bankcards.exception;

import java.util.UUID;

public class CardNotFoundException extends RuntimeException {
    public CardNotFoundException(UUID cardId) {
        super("Card with ID: " + cardId + " was not found.");
    }

    public CardNotFoundException(String cardNumber) {
        super("Card with number " + cardNumber + " was not found.");
    }
}
