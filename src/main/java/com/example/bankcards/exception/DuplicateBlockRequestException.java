package com.example.bankcards.exception;

public class DuplicateBlockRequestException extends RuntimeException {
    public DuplicateBlockRequestException() {
        super("A block request for this card is already active.");
    }
}
