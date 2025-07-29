package com.example.bankcards.service;

import com.example.bankcards.dto.CardFilter;
import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public interface CardService {

    Card createCard(CardRequest cardRequest);

    Card getCardById(UUID id);

    Page<Card> getAllCards(CardFilter cardFilter);

    Card blockCard(UUID cardId);

    Card activateCard(UUID cardId);

    void deleteCard(UUID id);

    Page<Card> getAllMyCards(UUID userId, CardFilter cardFilter);

    void requestCardBlock(UUID cardId, UUID currentUserId);

    void transferBetweenMyCards(TransferRequest request, UUID currentUserId);

    BigDecimal getMyCardBalance(String cardNumber, UUID currentUserId);
}
