package com.example.bankcards.service.impl;

import com.example.bankcards.dto.UserRequest;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserAlreadyExistsException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse createUser(UserRequest request) {
        String email = request.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(email);
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(email)
                .phoneNumber(request.getPhoneNumber())
                .password(request.getPassword()) // хешировать ? при необходимости
                .role(request.getRole())
                .createdAt(LocalDateTime.now())
                .build();

        return mapToResponse(userRepository.save(user));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUser(UUID id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(request.getRole());

        return mapToResponse(userRepository.save(user));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUserById(UUID id) {
        return userRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .build();
    }
}
