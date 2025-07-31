package com.example.bankcards.controller;

import com.example.bankcards.dto.UserRequest;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Управление пользователями")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Создать пользователя")
    @ApiResponse(responseCode = "201", description = "Пользователь создан",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)))
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public UserResponse createUser(@RequestBody @Valid UserRequest request) {
        return userService.createUser(request);
    }

    @Operation(summary = "Обновить пользователя по ID")
    @ApiResponse(responseCode = "200", description = "Пользователь обновлён",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)))
    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable UUID id,
                                   @RequestBody @Valid UserRequest request) {
        return userService.updateUser(id, request);
    }

    @Operation(summary = "Удалить пользователя по ID")
    @ApiResponse(responseCode = "204", description = "Пользователь удалён")
    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }

    @Operation(summary = "Получить всех пользователей")
    @ApiResponse(responseCode = "200", description = "Список пользователей",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)))
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Получить пользователя по ID")
    @ApiResponse(responseCode = "200", description = "Пользователь найден",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)))
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }
}
