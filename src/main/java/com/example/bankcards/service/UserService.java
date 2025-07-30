package com.example.bankcards.service;

import com.example.bankcards.dto.UserRequest;
import com.example.bankcards.dto.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse createUser(UserRequest request);

    UserResponse updateUser(UUID id, UserRequest request);

    void deleteUser(UUID id);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(UUID id);
}
