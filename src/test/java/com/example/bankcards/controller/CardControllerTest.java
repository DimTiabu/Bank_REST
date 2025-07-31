package com.example.bankcards.controller;

import com.example.bankcards.dto.CardFilter;
import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.security.AppUserDetails;
import com.example.bankcards.security.JwtServiceImpl;
import com.example.bankcards.security.UserDetailsServiceImpl;
import com.example.bankcards.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
@AutoConfigureMockMvc(addFilters = false)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtServiceImpl jwtService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private CardService cardService;

    private final ObjectMapper mapper = new ObjectMapper();

    private UUID currentUserId;

    @BeforeEach
    void setupSecurityContext() {
        currentUserId = UUID.randomUUID();
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(new AppUserDetails(currentUserId, "user@mail.com", "pass", List.of()));
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createCard_shouldReturnCreatedCard() throws Exception {
        UUID userId = UUID.randomUUID();
        CardRequest request = CardRequest.builder()
                .userId(userId)
                .balance(new BigDecimal("500.00"))
                .build();

        CardResponse response = CardResponse.builder()
                .id(UUID.randomUUID())
                .number("1111222233334444")
                .expirationDate(LocalDate.now())
                .status(CardStatus.ACTIVE)
                .balance(new BigDecimal("500.00"))
                .build();

        when(cardService.createCard(any())).thenReturn(response);

        mockMvc.perform(post("/api/cards")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.number").value("1111222233334444"));
    }

    @Test
    void getCardById_shouldReturnCard() throws Exception {
        UUID id = UUID.randomUUID();
        CardResponse response = CardResponse.builder()
                .id(id)
                .number("1234567890123456")
                .expirationDate(LocalDate.now())
                .status(CardStatus.ACTIVE)
                .balance(new BigDecimal("1000.00"))
                .build();

        when(cardService.getCardById(id)).thenReturn(response);

        mockMvc.perform(get("/api/cards/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.balance").value("1000.0"));
    }

    @Test
    void getAllCards_shouldReturnPage() throws Exception {
        CardResponse response = CardResponse.builder()
                .id(UUID.randomUUID())
                .number("1111222233334444")
                .expirationDate(LocalDate.now())
                .status(CardStatus.ACTIVE)
                .balance(new BigDecimal("500.00"))
                .build();

        when(cardService.getAllCards(any(CardFilter.class)))
                .thenReturn(new PageImpl<>(List.of(response), PageRequest.of(0, 5), 1));

        mockMvc.perform(get("/api/cards?pageNumber=0&pageSize=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void blockCard_shouldReturnBlockedCard() throws Exception {
        UUID cardId = UUID.randomUUID();
        CardResponse response = CardResponse.builder()
                .id(cardId)
                .status(CardStatus.BLOCKED)
                .build();

        when(cardService.blockCard(cardId)).thenReturn(response);

        mockMvc.perform(put("/api/cards/{cardId}/block", cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BLOCKED"));
    }

    @Test
    void activateCard_shouldReturnActiveCard() throws Exception {
        UUID cardId = UUID.randomUUID();
        CardResponse response = CardResponse.builder()
                .id(cardId)
                .status(CardStatus.ACTIVE)
                .build();

        when(cardService.activateCard(cardId)).thenReturn(response);

        mockMvc.perform(put("/api/cards/{cardId}/activate", cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void deleteCard_shouldReturnNoContent() throws Exception {
        UUID cardId = UUID.randomUUID();

        doNothing().when(cardService).deleteCard(cardId);

        mockMvc.perform(delete("/api/cards/{id}", cardId))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllMyCards_shouldReturnPage() throws Exception {
        CardResponse response = CardResponse.builder()
                .id(UUID.randomUUID())
                .build();

        when(cardService.getAllMyCards(eq(currentUserId), any(CardFilter.class)))
                .thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/api/cards/user/{userId}", currentUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void requestCardBlock_shouldCallService() throws Exception {
        UUID cardId = UUID.randomUUID();

        doNothing().when(cardService).requestCardBlock(cardId, currentUserId);

        mockMvc.perform(put("/api/cards/{cardId}/request-block", cardId))
                .andExpect(status().isOk());

        verify(cardService).requestCardBlock(cardId, currentUserId);
    }

    @Test
    void transferBetweenMyCards_shouldCallService() throws Exception {
        TransferRequest request = new TransferRequest();
        request.setFromCardId(UUID.randomUUID());
        request.setToCardId(UUID.randomUUID());
        request.setAmount(new BigDecimal("100.00"));

        doNothing().when(cardService).transferBetweenMyCards(request, currentUserId);

        mockMvc.perform(post("/api/cards/transfer")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(cardService).transferBetweenMyCards(request, currentUserId);
    }


    @Test
    void getMyCardBalance_shouldReturnBalance() throws Exception {
        UUID cardId = UUID.randomUUID();

        when(cardService.getMyCardBalance(cardId, currentUserId)).thenReturn(new BigDecimal("200.00"));

        mockMvc.perform(get("/api/cards/balance/{cardId}", cardId))
                .andExpect(status().isOk())
                .andExpect(content().string("200.00"));
    }
}
