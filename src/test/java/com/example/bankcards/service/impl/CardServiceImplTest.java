package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CardFilter;
import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.*;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.StatusAlreadySetException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardBlockRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardMapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceImplTest {

    @Mock private CardRepository cardRepository;
    @Mock private UserRepository userRepository;
    @Mock private CardBlockRequestRepository blockRequestRepo;

    @InjectMocks
    private CardServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCard_shouldSaveCard() {
        UUID userId = UUID.randomUUID();
        CardRequest request = CardRequest.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(5000))
                .build();

        User user = new User();
        Card card = CardMapperFactory.toCard(user, request.getBalance());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        var result = service.createCard(request);

        assertThat(result).isNotNull();
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void createCard_shouldThrowException_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        CardRequest request = CardRequest.builder()
                .userId(userId)
                .balance(BigDecimal.valueOf(1000))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createCard(request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(userId.toString());
    }

    @Test
    void getCardById_shouldReturnCard() {
        UUID cardId = UUID.randomUUID();
        Card card = new Card();
        card.setId(cardId);
        card.setBalance(BigDecimal.TEN);
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        var result = service.getCardById(cardId);

        assertThat(result.getId()).isEqualTo(cardId);
    }

    @Test
    void getCardById_shouldThrowException_whenCardNotFound() {
        UUID cardId = UUID.randomUUID();
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getCardById(cardId))
                .isInstanceOf(CardNotFoundException.class)
                .hasMessageContaining(cardId.toString());
    }

    @Test
    void transferBetweenMyCards_shouldTransferFunds() {
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = User.builder().id(userId).build();

        Card fromCard = Card.builder()
                .id(fromId)
                .user(user)
                .balance(BigDecimal.valueOf(100))
                .status(CardStatus.ACTIVE)
                .build();

        Card toCard = Card.builder()
                .id(toId)
                .user(user)
                .balance(BigDecimal.valueOf(50))
                .status(CardStatus.ACTIVE)
                .build();

        TransferRequest request = new TransferRequest();
        request.setFromCardId(fromId);
        request.setToCardId(toId);
        request.setAmount(BigDecimal.valueOf(30));

        when(cardRepository.findByIdAndUserId(fromId, userId)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndUserId(toId, userId)).thenReturn(Optional.of(toCard));

        service.transferBetweenMyCards(request, userId);

        assertThat(fromCard.getBalance()).isEqualByComparingTo("70");
        assertThat(toCard.getBalance()).isEqualByComparingTo("80");
        verify(cardRepository, times(2)).save(any(Card.class));
    }

    @Test
    void transferBetweenMyCards_shouldThrowException_whenFromCardNotFound() {
        UUID userId = UUID.randomUUID();
        TransferRequest request = new TransferRequest();
        request.setFromCardId(UUID.randomUUID());
        request.setToCardId(UUID.randomUUID());
        request.setAmount(BigDecimal.valueOf(50));

        when(cardRepository.findByIdAndUserId(request.getFromCardId(), userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.transferBetweenMyCards(request, userId))
                .isInstanceOf(CardNotFoundException.class);
    }

    @Test
    void getMyCardBalance_shouldReturnBalance() {
        UUID cardId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = User.builder().id(userId).build();

        Card card = Card.builder()
                .id(cardId)
                .user(user)
                .balance(BigDecimal.valueOf(123.45))
                .status(CardStatus.ACTIVE)
                .build();

        when(cardRepository.findByIdAndUserId(cardId, userId)).thenReturn(Optional.of(card));

        BigDecimal balance = service.getMyCardBalance(cardId, userId);

        assertThat(balance).isEqualByComparingTo("123.45");
    }

    @Test
    void getMyCardBalance_shouldThrowException_whenCardNotFound() {
        UUID cardId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(cardRepository.findByIdAndUserId(cardId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getMyCardBalance(cardId, userId))
                .isInstanceOf(CardNotFoundException.class);
    }

    @Test
    void requestCardBlock_shouldSaveRequest() {
        UUID cardId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = User.builder().id(userId).build();

        Card card = Card.builder()
                .id(cardId)
                .user(user)
                .status(CardStatus.ACTIVE)
                .build();

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(blockRequestRepo.existsByCardIdAndStatus(cardId, CardBlockRequest.RequestStatus.ACTIVE))
                .thenReturn(false);

        service.requestCardBlock(cardId, userId);

        verify(blockRequestRepo).save(any(CardBlockRequest.class));
    }

    @Test
    void requestCardBlock_shouldThrowException_whenCardNotFound() {
        UUID cardId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.requestCardBlock(cardId, userId))
                .isInstanceOf(CardNotFoundException.class);
    }

    @Test
    void requestCardBlock_shouldThrowException_whenRequestAlreadyExists() {
        UUID cardId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Card card = Card.builder()
                .id(cardId)
                .user(User.builder().id(userId).build())
                .status(CardStatus.ACTIVE)
                .build();

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(blockRequestRepo.existsByCardIdAndStatus(cardId, CardBlockRequest.RequestStatus.ACTIVE))
                .thenReturn(true);

        assertThatThrownBy(() -> service.requestCardBlock(cardId, userId))
                .isInstanceOf(com.example.bankcards.exception.DuplicateBlockRequestException.class)
                .hasMessageContaining("A block request for this card is already active");
    }

    @Test
    void getAllCards_shouldReturnPagedCards() {
        CardFilter filter = new CardFilter();
        filter.setPageNumber(0);
        filter.setPageSize(10);

        Card card = new Card();
        card.setId(UUID.randomUUID());
        card.setNumber("1234 5678 9012 3456");
        card.setStatus(CardStatus.ACTIVE);

        Page<Card> page = new PageImpl<>(List.of(card));

        when(cardRepository.findAll(ArgumentMatchers.<Specification<Card>>any(), any(Pageable.class))).thenReturn(page);

        Page<CardResponse> result = service.getAllCards(filter);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getNumber()).endsWith("3456");

        verify(cardRepository).findAll(ArgumentMatchers.<Specification<Card>>any(), any(Pageable.class));
    }

    @Test
    void blockCard_shouldUpdateStatusToBlocked() {
        UUID cardId = UUID.randomUUID();

        Card card = new Card();
        card.setId(cardId);
        card.setNumber("1111 2222 3333 4444");
        card.setStatus(CardStatus.ACTIVE);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenAnswer(i -> i.getArgument(0));

        CardResponse response = service.blockCard(cardId);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(CardStatus.BLOCKED);

        verify(cardRepository).findById(cardId);
        verify(cardRepository).save(card);
    }

    @Test
    void blockCard_shouldThrowException_whenStatusAlreadyBlocked() {
        UUID cardId = UUID.randomUUID();

        Card card = new Card();
        card.setId(cardId);
        card.setNumber("1111 2222 3333 4444");
        card.setStatus(CardStatus.BLOCKED);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        assertThatThrownBy(() -> service.blockCard(cardId))
                .isInstanceOf(StatusAlreadySetException.class)
                .hasMessageContaining(card.getNumber());
    }

    @Test
    void activateCard_shouldUpdateStatusToActive() {
        UUID cardId = UUID.randomUUID();

        Card card = new Card();
        card.setId(cardId);
        card.setNumber("5555 6666 7777 8888");
        card.setStatus(CardStatus.BLOCKED);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenAnswer(i -> i.getArgument(0));

        CardResponse response = service.activateCard(cardId);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(CardStatus.ACTIVE);

        verify(cardRepository).findById(cardId);
        verify(cardRepository).save(card);
    }

    @Test
    void activateCard_shouldThrowException_whenStatusAlreadyActive() {
        UUID cardId = UUID.randomUUID();

        Card card = new Card();
        card.setId(cardId);
        card.setNumber("5555 6666 7777 8888");
        card.setStatus(CardStatus.ACTIVE);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        assertThatThrownBy(() -> service.activateCard(cardId))
                .isInstanceOf(StatusAlreadySetException.class)
                .hasMessageContaining(card.getNumber());
    }

    @Test
    void deleteCard_shouldDelete_whenExists() {
        UUID cardId = UUID.randomUUID();

        when(cardRepository.existsById(cardId)).thenReturn(true);

        service.deleteCard(cardId);

        verify(cardRepository).existsById(cardId);
        verify(cardRepository).deleteById(cardId);
    }

    @Test
    void deleteCard_shouldThrowException_whenNotFound() {
        UUID cardId = UUID.randomUUID();

        when(cardRepository.existsById(cardId)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteCard(cardId))
                .isInstanceOf(CardNotFoundException.class)
                .hasMessageContaining(cardId.toString());

        verify(cardRepository).existsById(cardId);
        verify(cardRepository, never()).deleteById(any());
    }

    @Test
    void getAllMyCards_shouldReturnPagedCardsForUser() {
        UUID userId = UUID.randomUUID();
        CardFilter filter = new CardFilter();
        filter.setPageNumber(0);
        filter.setPageSize(10);

        Card card = new Card();
        card.setId(UUID.randomUUID());
        card.setUser(null);
        card.setNumber("9999 8888 7777 6666");

        Page<Card> page = new PageImpl<>(List.of(card));

        when(cardRepository.findAll(ArgumentMatchers.<Specification<Card>>any(), any(Pageable.class)))
                .thenReturn(page);

        Page<CardResponse> result = service.getAllMyCards(userId, filter);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        assertThat(result.getContent().get(0).getNumber()).endsWith("6666");

        assertThat(filter.getUserId()).isEqualTo(userId);

        verify(cardRepository).findAll(ArgumentMatchers.<Specification<Card>>any(), any(Pageable.class));
    }
}
