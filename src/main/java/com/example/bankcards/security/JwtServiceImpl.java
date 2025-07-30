package com.example.bankcards.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtServiceImpl {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.tokenExpiration}")
    private Duration tokenExpiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
    public String generateJwtToken(AppUserDetails userDetails) {
        String jwt = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("userId", userDetails.getId())
                .claim("roles", userDetails.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration.toMillis()))
                .setHeaderParam("typ", "JWT")
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        log.info("Создан jwt для пользователя {}", userDetails.getUsername());
        return jwt;
    }

    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validate(String authToken) {
        boolean result = true;

        try {
            String email = getUsername(authToken);
            log.info("Запущен метод validate для пользователя {}", email);

            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(authToken);

        } catch (SignatureException e) {
            log.error("Недопустимая подпись: {}", e.getMessage());
            result = false;
        } catch (MalformedJwtException e) {
            log.error("Недопустимый токен: {}", e.getMessage());
            result = false;
        } catch (UnsupportedJwtException e) {
            log.error("Токен не поддерживается: {}", e.getMessage());
            result = false;
        } catch (ExpiredJwtException e) {
            log.error("Токен просрочен");
            result = false;
        }

        return result;
    }
}