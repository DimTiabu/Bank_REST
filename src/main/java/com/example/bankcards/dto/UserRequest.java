package com.example.bankcards.dto;

import com.example.bankcards.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "Запрос на создание или обновление пользователя")
public class UserRequest {

    @NotEmpty(message = "Укажите имя")
    @Schema(description = "Имя пользователя", example = "Иван")
    private String firstName;

    @NotEmpty(message = "Укажите фамилию")
    @Schema(description = "Фамилия пользователя", example = "Иванов")
    private String lastName;

    @NotEmpty(message = "Укажите электронную почту")
    @Email(message = "Неправильный формат электронной почты")
    @Schema(description = "Электронная почта пользователя", example = "ivanov@example.com")
    private String email;

    @NotEmpty(message = "Укажите номер телефона")
    @Pattern(regexp = "^(89\\d{9})$", message = "Укажите номер телефона в формате 89********* ")
    @Schema(description = "Номер телефона в формате 89*********", example = "89161234567")
    private String phoneNumber;

    @NotEmpty(message = "Укажите пароль")
    @Schema(description = "Пароль пользователя", example = "P@ssw0rd123")
    private String password;

    @Builder.Default
    @Schema(description = "Дата и время создания пользователя", example = "2025-07-31T10:15:30")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Schema(description = "Роль пользователя", example = "ROLE_USER")
    private UserRole role = UserRole.ROLE_USER;
}
