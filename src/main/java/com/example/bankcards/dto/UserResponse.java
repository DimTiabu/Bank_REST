package com.example.bankcards.dto;

import com.example.bankcards.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Ответ с данными пользователя")
public class UserResponse {

    @Schema(description = "Уникальный идентификатор пользователя", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Имя пользователя", example = "Иван")
    private String firstName;

    @Schema(description = "Фамилия пользователя", example = "Иванов")
    private String lastName;

    @Schema(description = "Электронная почта пользователя", example = "ivanov@example.com")
    private String email;

    @Schema(description = "Номер телефона пользователя", example = "89161234567")
    private String phoneNumber;

    @Schema(description = "Роль пользователя", example = "ROLE_USER")
    private UserRole role;
}
