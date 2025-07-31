package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Запрос для аутентификации пользователя")
public class AuthRequest {

    @NotBlank
    @Email
    @Schema(description = "Email пользователя", example = "user@example.com", required = true)
    private String email;

    @NotBlank
    @Schema(description = "Пароль пользователя", example = "strongPassword123", required = true)
    private String password;

}
