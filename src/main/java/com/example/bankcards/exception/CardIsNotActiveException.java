package com.example.bankcards.exception;

public class CardIsNotActiveException extends RuntimeException {
    public CardIsNotActiveException() {
        super("Обе карты должны быть активными");
    }
}
