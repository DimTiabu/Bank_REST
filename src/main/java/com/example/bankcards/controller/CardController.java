package com.example.bankcards.controller;

import com.example.bankcards.dto.CardFilter;
import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.security.AppUserDetails;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Tag(name = "Cards", description = "Управление банковскими картами")
public class CardController {

    private final CardService cardService;

    @Operation(summary = "Создание карты")
    @ApiResponse(responseCode = "201", description = "Карта создана",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CardResponse.class)))
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CardResponse createCard(@RequestBody @Valid CardRequest cardRequest) {
        return cardService.createCard(cardRequest);
    }

    @Operation(summary = "Получить карту по ID")
    @ApiResponse(responseCode = "200", description = "Карта найдена",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CardResponse.class)))
    @GetMapping("/{id}")
    public CardResponse getCardById(@PathVariable UUID id) {
        return cardService.getCardById(id);
    }

    @Operation(summary = "Получить все карты с фильтрацией и пагинацией")
    @ApiResponse(responseCode = "200", description = "Список карт",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    @GetMapping
    public Page<CardResponse> getAllCards(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "5") int pageSize,
            CardFilter cardFilter) {
        cardFilter.setPageNumber(pageNumber);
        cardFilter.setPageSize(pageSize);
        return cardService.getAllCards(cardFilter);
    }

    @Operation(summary = "Заблокировать карту")
    @ApiResponse(responseCode = "200", description = "Карта заблокирована",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CardResponse.class)))
    @PutMapping("/{cardId}/block")
    public CardResponse blockCard(@PathVariable UUID cardId) {
        return cardService.blockCard(cardId);
    }

    @Operation(summary = "Активировать карту")
    @ApiResponse(responseCode = "200", description = "Карта активирована",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CardResponse.class)))
    @PutMapping("/{cardId}/activate")
    public CardResponse activateCard(@PathVariable UUID cardId) {
        return cardService.activateCard(cardId);
    }

    @Operation(summary = "Удалить карту по ID")
    @ApiResponse(responseCode = "204", description = "Карта удалена")
    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteCard(@PathVariable UUID id) {
        cardService.deleteCard(id);
    }

    @Operation(summary = "Получить все карты пользователя")
    @ApiResponse(responseCode = "200", description = "Список карт пользователя",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    @GetMapping("/user/{userId}")
    public Page<CardResponse> getAllMyCards(
            @PathVariable UUID userId,
            CardFilter cardFilter) {
        return cardService.getAllMyCards(userId, cardFilter);
    }

    @Operation(summary = "Запрос на блокировку карты (пользовательский)")
    @ApiResponse(responseCode = "200", description = "Запрос на блокировку отправлен")
    @PutMapping("/{cardId}/request-block")
    public void requestCardBlock(@PathVariable UUID cardId) {
        UUID currentUserId = getCurrentUserIdFromSecurityContext();
        cardService.requestCardBlock(cardId, currentUserId);
    }

    @Operation(summary = "Перевод между своими картами")
    @ApiResponse(responseCode = "200", description = "Перевод выполнен")
    @PostMapping("/transfer")
    public void transferBetweenMyCards(@RequestBody TransferRequest request) {
        UUID currentUserId = getCurrentUserIdFromSecurityContext();
        cardService.transferBetweenMyCards(request, currentUserId);
    }

    @Operation(summary = "Получить баланс своей карты")
    @ApiResponse(responseCode = "200", description = "Баланс возвращён",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BigDecimal.class)))
    @GetMapping("/balance/{cardId}")
    public BigDecimal getMyCardBalance(@PathVariable UUID cardId) {
        UUID currentUserId = getCurrentUserIdFromSecurityContext();
        return cardService.getMyCardBalance(cardId, currentUserId);
    }

    private UUID getCurrentUserIdFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof AppUserDetails userDetails) {
            return userDetails.getId();
        }
        throw new IllegalStateException("Invalid user principal");
    }
}
