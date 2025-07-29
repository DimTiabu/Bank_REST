package com.example.bankcards.entity;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException() {
        super("Недостаточно средств на карте");
    }
}