package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "Запрос на создание новой карты")
public class CardRequest {

    @Schema(description = "ID пользователя, которому принадлежит карта", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", required = true)
    private UUID userId;

    @Schema(description = "Начальный баланс карты", example = "5000.00", required = true)
    private BigDecimal balance;
}
