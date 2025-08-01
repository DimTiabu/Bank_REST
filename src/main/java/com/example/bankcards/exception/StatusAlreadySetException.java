package com.example.bankcards.exception;

import com.example.bankcards.entity.CardStatus;

public class StatusAlreadySetException extends RuntimeException {
    public StatusAlreadySetException(String cardNumber, CardStatus status) {
        super("Card " + cardNumber + " already has status " + status);
    }
}
