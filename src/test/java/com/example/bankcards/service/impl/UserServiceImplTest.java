package com.example.bankcards.service.impl;

import com.example.bankcards.dto.UserRequest;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.UserRole;
import com.example.bankcards.exception.EmailAlreadyExistsException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.UserMapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_shouldSaveUser_whenEmailNotExists() {
        UserRequest request = UserRequest.builder()
                .email("test@mail.com")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("1234567890")
                .role(UserRole.ROLE_USER)
                .build();

        when(userRepository.existsByEmail("test@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPass");

        User savedUser = UserMapperFactory.toUser(request, "encodedPass");
        savedUser.setId(UUID.randomUUID());

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = service.createUser(request);

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("test@mail.com");

        verify(userRepository).existsByEmail("test@mail.com");
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowException_whenEmailExists() {
        UserRequest request = UserRequest.builder().email("exists@mail.com").build();

        when(userRepository.existsByEmail("exists@mail.com")).thenReturn(true);

        assertThatThrownBy(() -> service.createUser(request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("exists@mail.com");

        verify(userRepository).existsByEmail("exists@mail.com");
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void updateUser_shouldUpdateUser_whenValidRequest() {
        UUID userId = UUID.randomUUID();
        UserRequest request = UserRequest.builder()
                .email("new@mail.com")
                .password("newpass")
                .firstName("New")
                .lastName("Name")
                .phoneNumber("0987654321")
                .role(UserRole.ROLE_ADMIN)
                .build();

        User existingUser = User.builder()
                .id(userId)
                .email("old@mail.com")
                .password("oldpass")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("new@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newpass")).thenReturn("encodedNewPass");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserResponse updated = service.updateUser(userId, request);

        assertThat(updated).isNotNull();
        assertThat(updated.getEmail()).isEqualTo("new@mail.com");

        verify(userRepository).findById(userId);
        verify(userRepository).findByEmail("new@mail.com");
        verify(passwordEncoder).encode("newpass");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_shouldThrowException_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        UserRequest request = UserRequest.builder().email("email@mail.com").build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateUser(userId, request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(userId.toString());

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_shouldThrowException_whenEmailAlreadyUsedByAnotherUser() {
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();

        UserRequest request = UserRequest.builder().email("duplicate@mail.com").build();

        User existingUser = User.builder().id(userId).build();
        User otherUser = User.builder().id(otherUserId).email("duplicate@mail.com").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("duplicate@mail.com")).thenReturn(Optional.of(otherUser));

        assertThatThrownBy(() -> service.updateUser(userId, request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("duplicate@mail.com");

        verify(userRepository).findById(userId);
        verify(userRepository).findByEmail("duplicate@mail.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_shouldDelete_whenUserExists() {
        UUID userId = UUID.randomUUID();

        when(userRepository.existsById(userId)).thenReturn(true);

        service.deleteUser(userId);

        verify(userRepository).existsById(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_shouldThrowException_whenUserNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteUser(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(userId.toString());

        verify(userRepository).existsById(userId);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void getAllUsers_shouldReturnList() {
        User user1 = User.builder().id(UUID.randomUUID()).email("a@mail.com").build();
        User user2 = User.builder().id(UUID.randomUUID()).email("b@mail.com").build();

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserResponse> users = service.getAllUsers();

        assertThat(users).hasSize(2);
        assertThat(users).extracting("email").containsExactlyInAnyOrder("a@mail.com", "b@mail.com");

        verify(userRepository).findAll();
    }

    @Test
    void getUserById_shouldReturnUser() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .email("found@mail.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponse response = service.getUserById(userId);

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("found@mail.com");

        verify(userRepository).findById(userId);
    }

    @Test
    void getUserById_shouldThrowException_whenNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getUserById(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(userId.toString());

        verify(userRepository).findById(userId);
    }
}
