package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardFilter {
    @Builder.Default
    private Integer pageSize = 5;

    @Builder.Default
    private Integer pageNumber = 0;

    private UUID id;

    private String encryptedNumber;

    private User user;

    private LocalDate expirationDateFrom;
    private LocalDate expirationDateTo;

    private CardStatus status;

    private BigDecimal balanceFrom;
    private BigDecimal balanceTo;
}
