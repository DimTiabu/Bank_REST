package com.example.bankcards.service;

import com.example.bankcards.dto.CardFilter;
import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.TransferRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public interface CardService {

    CardResponse createCard(CardRequest cardRequest);

    CardResponse getCardById(UUID id);

    Page<CardResponse> getAllCards(CardFilter cardFilter);

    CardResponse blockCard(UUID cardId);

    CardResponse activateCard(UUID cardId);

    void deleteCard(UUID id);

    Page<CardResponse> getAllMyCards(UUID userId, CardFilter cardFilter);

    void requestCardBlock(UUID cardId, UUID currentUserId);

    void transferBetweenMyCards(TransferRequest request, UUID currentUserId);

    BigDecimal getMyCardBalance(UUID cardId, UUID currentUserId);
}
