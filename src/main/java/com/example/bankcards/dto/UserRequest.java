package com.example.bankcards.dto;

import com.example.bankcards.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

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

    @NotEmpty(message = "Укажите электронную почту")
    private String phoneNumber;

    @NotEmpty(message = "Укажите электронную почту")
    private String password;

    @Builder.Default
    private UserRole role = UserRole.ROLE_USER;
}
