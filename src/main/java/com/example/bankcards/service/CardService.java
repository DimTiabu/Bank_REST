package com.example.bankcards.service;

import com.example.bankcards.dto.CardFilter;
import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface CardService {
    Card createCard(CardRequest cardRequest);
    Card getCardById(UUID id);
    Page<Card> getAllCards(CardFilter cardFilter);
    Page<Card> getUserCards(UUID userId, Pageable pageable);
    Card blockCard(UUID cardId);
    Card activateCard(UUID cardId);
    void deleteCard(UUID id);
}
