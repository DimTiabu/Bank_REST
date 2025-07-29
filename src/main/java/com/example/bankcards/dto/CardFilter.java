package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CardFilter {
    @Builder.Default
    private Integer pageSize = 5;

    @Builder.Default
    private Integer pageNumber = 0;

    private UUID id;

    private String encryptedNumber;

    private UUID userId;

    private LocalDate expirationDateFrom;
    private LocalDate expirationDateTo;

    private CardStatus status;

    private BigDecimal balanceFrom;
    private BigDecimal balanceTo;
}