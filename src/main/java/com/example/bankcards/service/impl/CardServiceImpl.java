package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CardFilter;
import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.StatusAlreadySetException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardSpecification;
import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Card createCard(CardRequest cardRequest) {
        return cardRepository.save(
                Card.builder()
                        .id(UUID.randomUUID())
                        .encryptedNumber(cardRequest.getCardNumber())
                        .user(cardRequest.getUser())
                        .expirationDate(LocalDate.now().plusYears(3))
                        .status(CardStatus.ACTIVE)
                        .balance(BigDecimal.ZERO)
                        .build());
    }

    @Override
    @Transactional(readOnly = true)
    public Card getCardById(UUID id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Page<Card> getAllCards(CardFilter cardFilter) {
        return cardRepository.findAll(
                CardSpecification.withFilter(cardFilter),
                PageRequest.of(cardFilter.getPageNumber(), cardFilter.getPageSize())
        );
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Page<Card> getUserCards(UUID userId, Pageable pageable) {
        return cardRepository.findByUser(userId, pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Card blockCard(UUID cardId) {
        return updateCardStatus(cardId, CardStatus.BLOCKED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Card activateCard(UUID cardId) {
        return updateCardStatus(cardId, CardStatus.ACTIVE);
    }

    public Card updateCardStatus(UUID cardId, CardStatus status) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));
        String cardNumber = card.getEncryptedNumber();

        if (card.getStatus() == status) {
            throw new StatusAlreadySetException(cardNumber, status);
        }

        card.setStatus(status);
        return cardRepository.save(card);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCard(UUID id) {
        if (!cardRepository.existsById(id)) {
            throw new CardNotFoundException(id);
        }
        cardRepository.deleteById(id);
    }
}
