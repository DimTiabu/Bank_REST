package com.example.bankcards.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {
    private String fromCardNumber;
    private String toCardNumber;
    private BigDecimal amount;
}
