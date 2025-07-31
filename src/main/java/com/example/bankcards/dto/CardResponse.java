package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "Ответ с информацией о карте")
public class CardResponse {

    @Schema(description = "Уникальный идентификатор карты", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
    private UUID id;

    @Schema(description = "Номер карты в формате маскированного отображения", example = "**** **** **** 1234")
    private String number;

    @Schema(description = "Срок действия карты", example = "2026-12-31")
    private LocalDate expirationDate;

    @Schema(description = "Статус карты", example = "ACTIVE")
    private CardStatus status;

    @Schema(description = "Текущий баланс карты", example = "1500.00")
    private BigDecimal balance;
}
