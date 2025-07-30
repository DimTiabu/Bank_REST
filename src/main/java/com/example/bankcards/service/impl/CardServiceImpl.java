package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CardFilter;
import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.InsufficientFundsException;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardIsNotActiveException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.StatusAlreadySetException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardSpecification;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.CardMapperFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public CardResponse createCard(CardRequest cardRequest) {
        UUID userId = cardRequest.getUserId();
        log.info("userId = " + userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Card saved = cardRepository.save(CardMapperFactory.toCard(cardRequest, user));
        return CardMapperFactory.toCardResponse(saved);
    }


    @Override
    public CardResponse getCardById(UUID id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
        return CardMapperFactory.toCardResponse(card);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Page<CardResponse> getAllCards(CardFilter cardFilter) {
        return cardRepository.findAll(
                CardSpecification.withFilter(cardFilter),
                PageRequest.of(cardFilter.getPageNumber(), cardFilter.getPageSize())
        ).map(CardMapperFactory::toCardResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public CardResponse blockCard(UUID cardId) {
        return updateCardStatus(cardId, CardStatus.BLOCKED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public CardResponse activateCard(UUID cardId) {
        return updateCardStatus(cardId, CardStatus.ACTIVE);
    }

    private CardResponse updateCardStatus(UUID cardId, CardStatus status) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));
        String cardNumber = card.getNumber();

        if (card.getStatus() == status) {
            throw new StatusAlreadySetException(cardNumber, status);
        }

        card.setStatus(status);
        Card updated = cardRepository.save(card);
        return CardMapperFactory.toCardResponse(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void deleteCard(UUID id) {
        if (!cardRepository.existsById(id)) {
            throw new CardNotFoundException(id);
        }
        cardRepository.deleteById(id);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Override
    public Page<CardResponse> getAllMyCards(UUID currentUserId, CardFilter cardFilter) {
        cardFilter.setUserId(currentUserId);
        return cardRepository.findAll(
                CardSpecification.withFilter(cardFilter),
                PageRequest.of(cardFilter.getPageNumber(), cardFilter.getPageSize())
        ).map(CardMapperFactory::toCardResponse);
    }

    @PreAuthorize("hasRole('USER')")
    @Override
    public void requestCardBlock(UUID cardId, UUID currentUserId) {
        Card card = getCardByIdRaw(cardId);
        if (!card.getUser().getId().equals(currentUserId)) {
            throw new CardNotFoundException(cardId);
        }
        // TODO: реализация запроса на блокировку карты пользователем
    }

    @Override
    @Transactional
    public void transferBetweenMyCards(TransferRequest request, UUID currentUserId) {
        Card fromCard = getCardByCardNumberAndUserId(request.getFromCardNumber(), currentUserId);
        Card toCard = getCardByCardNumberAndUserId(request.getToCardNumber(), currentUserId);

        if (fromCard.getStatus() != CardStatus.ACTIVE || toCard.getStatus() != CardStatus.ACTIVE) {
            throw new CardIsNotActiveException();
        }

        if (fromCard.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException();
        }

        fromCard.setBalance(fromCard.getBalance().subtract(request.getAmount()));
        toCard.setBalance(toCard.getBalance().add(request.getAmount()));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);
    }

    @Override
    public BigDecimal getMyCardBalance(String cardNumber, UUID currentUserId) {
        Card card = getCardByCardNumberAndUserId(cardNumber, currentUserId);
        return card.getBalance();
    }

    private Card getCardByCardNumberAndUserId(String cardNumber, UUID currentUserId) {
        return cardRepository.findByNumberAndUserId(cardNumber, currentUserId)
                .orElseThrow(() -> new CardNotFoundException(cardNumber));
    }

    private Card getCardByIdRaw(UUID id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
    }
}
