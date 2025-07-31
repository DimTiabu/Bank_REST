package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Schema(description = "Запрос на перевод между картами")
public class TransferRequest {

    @Schema(description = "ID карты отправителя", example = "111e4567-e89b-12d3-a456-426614174000", required = true)
    private UUID fromCardId;

    @Schema(description = "ID карты получателя", example = "222e4567-e89b-12d3-a456-426614174001", required = true)
    private UUID toCardId;

    @Schema(description = "Сумма перевода", example = "500.00", required = true)
    private BigDecimal amount;
}
