package com.example.bankcards.dto;

import com.example.bankcards.entity.UserRole;
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
public class UserRequest {

    @NotEmpty(message = "Укажите имя")
    private String firstName;

    @NotEmpty(message = "Укажите фамилию")
    private String lastName;

    @NotEmpty(message = "Укажите электронную почту")
    @Email(message = "Неправильный формат электронной почты")
    private String email;

    @Pattern(regexp = "^(89\\d{9})$", message = "Укажите номер телефона в формате 89********* ")
    @NotEmpty(message = "Укажите номер телефона")
    private String phoneNumber;

    @NotEmpty(message = "Укажите пароль")
    private String password;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private UserRole role = UserRole.ROLE_USER;
}