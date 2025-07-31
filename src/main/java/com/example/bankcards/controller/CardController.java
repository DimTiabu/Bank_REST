package com.example.bankcards.controller;

import com.example.bankcards.dto.CardFilter;
import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.security.AppUserDetails;
import com.example.bankcards.service.CardService;
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
public class CardController {

    private final CardService cardService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CardResponse createCard(@RequestBody @Valid CardRequest cardRequest) {
        return cardService.createCard(cardRequest);
    }

    @GetMapping("/{id}")
    public CardResponse getCardById(@PathVariable UUID id) {
        return cardService.getCardById(id);
    }

    @GetMapping
    public Page<CardResponse> getAllCards(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "5") int pageSize,
            CardFilter cardFilter) {
        cardFilter.setPageNumber(pageNumber);
        cardFilter.setPageSize(pageSize);
        return cardService.getAllCards(cardFilter);
    }

    @PutMapping("/{cardId}/block")
    public CardResponse blockCard(@PathVariable UUID cardId) {
        return cardService.blockCard(cardId);
    }

    @PutMapping("/{cardId}/activate")
    public CardResponse activateCard(@PathVariable UUID cardId) {
        return cardService.activateCard(cardId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteCard(@PathVariable UUID id) {
        cardService.deleteCard(id);
    }

    @GetMapping("/user/{userId}")
    public Page<CardResponse> getAllMyCards(
            @PathVariable UUID userId,
            CardFilter cardFilter) {
        return cardService.getAllMyCards(userId, cardFilter);
    }

    @PutMapping("/{cardId}/request-block")
    public void requestCardBlock(@PathVariable UUID cardId) {
        UUID currentUserId = getCurrentUserIdFromSecurityContext();
        cardService.requestCardBlock(cardId, currentUserId);
    }

    @PostMapping("/transfer")
    public void transferBetweenMyCards(@RequestBody TransferRequest request) {
        UUID currentUserId = getCurrentUserIdFromSecurityContext();
        cardService.transferBetweenMyCards(request, currentUserId);
    }

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