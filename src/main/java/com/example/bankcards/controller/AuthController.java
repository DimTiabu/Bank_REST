package com.example.bankcards.controller;

import com.example.bankcards.dto.AuthRequest;
import com.example.bankcards.dto.AuthResponse;
import com.example.bankcards.exception.AuthenticationException;
import com.example.bankcards.security.AppUserDetails;
import com.example.bankcards.security.JwtServiceImpl;
import com.example.bankcards.security.UserDetailsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "JWT аутентификация пользователей")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtServiceImpl jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Operation(
            summary = "Аутентификация пользователя",
            description = "Проверяет учетные данные и возвращает JWT токен при успешной аутентификации"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная аутентификация",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Неверный логин или пароль",
                    content = @Content)
    })
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            throw new AuthenticationException();
        }

        AppUserDetails userDetails = (AppUserDetails) userDetailsService.loadUserByUsername(request.getEmail());

        String token = jwtService.generateJwtToken(userDetails);

        return new AuthResponse(token);
    }
}
