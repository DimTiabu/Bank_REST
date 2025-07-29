package com.example.bankcards.controller;

import com.example.bankcards.dto.CardFilter;
import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping
    @ResponseStatus(code = org.springframework.http.HttpStatus.CREATED)
    public Card createCard(@RequestBody CardRequest cardRequest) {
        return cardService.createCard(cardRequest);
    }

    @GetMapping("/{id}")
    public Card getCardById(@PathVariable UUID id) {
        return cardService.getCardById(id);
    }

    @GetMapping
    public Page<Card> getAllCards(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "5") int pageSize,
            CardFilter cardFilter) {
        cardFilter.setPageNumber(pageNumber);
        cardFilter.setPageSize(pageSize);
        return cardService.getAllCards(cardFilter);
    }

    @PutMapping("/{cardId}/block")
    public Card blockCard(@PathVariable UUID cardId) {
        return cardService.blockCard(cardId);
    }

    @PutMapping("/{cardId}/activate")
    public Card activateCard(@PathVariable UUID cardId) {
        return cardService.activateCard(cardId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = org.springframework.http.HttpStatus.NO_CONTENT)
    public void deleteCard(@PathVariable UUID id) {
        cardService.deleteCard(id);
    }

    @GetMapping("/user/{userId}")
    public Page<Card> getAllMyCards(
            @PathVariable UUID userId,
            CardFilter cardFilter) {
        return cardService.getAllMyCards(userId, cardFilter);
    }

    @PutMapping("/{cardId}/request-block")
    public void requestCardBlock(
            @PathVariable UUID cardId,
            @RequestParam UUID currentUserId
    ) {
        //TODO: Добавить извлечение из SecurityContext
        cardService.requestCardBlock(cardId, currentUserId);
    }

    @PostMapping("/transfer")
    public void transferBetweenMyCards(
            @RequestBody TransferRequest request,
            @RequestParam UUID currentUserId
            //TODO: Добавить извлечение из SecurityContext
    ) {
        cardService.transferBetweenMyCards(request, currentUserId);
    }

    @GetMapping("/balance")
    public BigDecimal getMyCardBalance(
            @RequestParam String cardNumber,
            @RequestParam UUID currentUserId
            //TODO: Добавить извлечение из SecurityContext
    ) {
        return cardService.getMyCardBalance(cardNumber, currentUserId);
    }
}