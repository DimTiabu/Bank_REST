package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "Фильтр для поиска карт по различным критериям")
public class CardFilter {

    @Builder.Default
    @Schema(description = "Размер страницы", example = "5")
    private Integer pageSize = 5;

    @Builder.Default
    @Schema(description = "Номер страницы (нумерация с 0)", example = "0")
    private Integer pageNumber = 0;

    @Schema(description = "ID карты", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID id;

    @Schema(description = "Зашифрованный номер карты", example = "**** **** **** 1234")
    private String encryptedNumber;

    @Schema(description = "ID владельца карты", example = "d2f0e6bc-1c2f-4b10-9d6b-313d23456789")
    private UUID userId;

    @Schema(description = "Дата окончания действия карты - от", example = "2025-01-01")
    private LocalDate expirationDateFrom;

    @Schema(description = "Дата окончания действия карты - до", example = "2025-12-31")
    private LocalDate expirationDateTo;

    @Schema(description = "Статус карты", example = "ACTIVE")
    private CardStatus status;

    @Schema(description = "Минимальный баланс карты", example = "1000.00")
    private BigDecimal balanceFrom;

    @Schema(description = "Максимальный баланс карты", example = "10000.00")
    private BigDecimal balanceTo;
}
