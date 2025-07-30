package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class CardResponse {
    private UUID id;
    private String number;
    private LocalDate expirationDate;
    private CardStatus status;
    private BigDecimal balance;
}
