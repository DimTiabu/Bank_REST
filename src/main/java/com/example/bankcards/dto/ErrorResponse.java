package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ об ошибке")
public class ErrorResponse {

    @Schema(description = "HTTP статус ошибки", example = "404")
    private int statusCode;

    @Schema(description = "Сообщение об ошибке", example = "Пользователь не найден")
    private String errorMessage;
}
