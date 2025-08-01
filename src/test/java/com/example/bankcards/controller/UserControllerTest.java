package com.example.bankcards.controller;

import com.example.bankcards.dto.UserRequest;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.entity.UserRole;
import com.example.bankcards.security.JwtServiceImpl;
import com.example.bankcards.security.UserDetailsServiceImpl;
import com.example.bankcards.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtServiceImpl jwtService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private UserServiceImpl userService;

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        UUID id = UUID.randomUUID();
        UserRequest request = UserRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@mail.com")
                .phoneNumber("89123456789")
                .password("password")
                .role(UserRole.ROLE_USER)
                .createdAt(null)
                .build();

        UserResponse response = UserResponse.builder()
                .id(id)
                .firstName("John")
                .lastName("Doe")
                .email("john@mail.com")
                .phoneNumber("89123456789")
                .role(UserRole.ROLE_USER)
                .build();

        when(userService.createUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.email").value("john@mail.com"));
    }

    @Test
    void getAllUsers_shouldReturnList() throws Exception {
        List<UserResponse> users = List.of(
                UserResponse.builder()
                        .id(UUID.randomUUID())
                        .firstName("John")
                        .lastName("Doe")
                        .email("john@mail.com")
                        .phoneNumber("89123456789")
                        .role(UserRole.ROLE_USER)
                        .build());

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        UUID id = UUID.randomUUID();

        UserRequest request = UserRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@mail.com")
                .phoneNumber("89123456780")
                .password("newpassword")
                .role(UserRole.ROLE_ADMIN)
                .createdAt(null)
                .build();

        UserResponse response = UserResponse.builder()
                .id(id)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@mail.com")
                .phoneNumber("89123456780")
                .role(UserRole.ROLE_ADMIN)
                .build();

        when(userService.updateUser(eq(id), any())).thenReturn(response);

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.email").value("jane@mail.com"));
    }

    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(userService).deleteUser(id);

        mockMvc.perform(delete("/api/users/{id}", id))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(id);
    }

    @Test
    void getUserById_shouldReturnUser() throws Exception {
        UUID id = UUID.randomUUID();

        UserResponse response = UserResponse.builder()
                .id(id)
                .firstName("Alice")
                .lastName("Brown")
                .email("alice@mail.com")
                .phoneNumber("89123456781")
                .role(UserRole.ROLE_USER)
                .build();

        when(userService.getUserById(id)).thenReturn(response);

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.email").value("alice@mail.com"));
    }

}
