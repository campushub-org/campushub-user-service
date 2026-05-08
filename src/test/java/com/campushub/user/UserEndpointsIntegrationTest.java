package com.campushub.user;

import com.campushub.user.dto.LoginRequest;
import com.campushub.user.dto.UserCreationRequest;
import com.campushub.user.dto.UserUpdateDto;
import com.campushub.user.model.Role;
import com.campushub.user.model.Student;
import com.campushub.user.model.User;
import com.campushub.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserEndpointsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    private Student createAndSaveStudent(String username, String email, String password) {
        Student student = new Student();
        student.setUsername(username);
        student.setEmail(email);
        student.setPassword(passwordEncoder.encode(password));
        student.setRole(Role.STUDENT);
        student.setFullName("Test Full Name");
        student.setStudentNumber("E12345");
        return userRepository.save(student);
    }

    // AUTH TESTS

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        UserCreationRequest request = new UserCreationRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setFullName("Test User");
        request.setEmail("test@email.com");
        request.setRole(Role.STUDENT);
        request.setStudentNumber("E12345");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        User user = userRepository.findByUsername("testuser").orElseThrow();
        assertThat(user.getFullName()).isEqualTo("Test User");
        assertThat(passwordEncoder.matches("password", user.getPassword())).isTrue();
    }

    @Test
    void shouldLoginSuccessfullyAndReturnToken() throws Exception {
        createAndSaveStudent("loginuser", "login@email.com", "password");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("loginuser");
        loginRequest.setPassword("password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void shouldFailLoginWithBadCredentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("nonexistent");
        loginRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized()); // Spring Security renvoie 401
    }

    // USER ENDPOINTS TESTS

    @Test
    @WithMockUser(username = "testuser", authorities = {"ROLE_STUDENT"})
    void shouldAllowAuthenticatedUserToListAllUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowUnauthenticatedUserToListAllUsers() throws Exception {
        // SecurityConfig permet GET /api/users/** sans authentification
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "owner", authorities = {"ROLE_STUDENT"})
    void shouldAllowUserToGetOwnDetails() throws Exception {
        User user = createAndSaveStudent("owner", "owner@email.com", "password");

        mockMvc.perform(get("/api/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("owner"));
    }

    @Test
    @WithMockUser(username = "other_user", authorities = {"ROLE_STUDENT"})
    void shouldForbidUserFromGettingAnotherUserDetails() throws Exception {
        User owner = createAndSaveStudent("owner", "owner@email.com", "password");

        mockMvc.perform(get("/api/users/" + owner.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void shouldAllowAdminToGetAnotherUserDetails() throws Exception {
        User owner = createAndSaveStudent("owner", "owner@email.com", "password");

        mockMvc.perform(get("/api/users/" + owner.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "owner", authorities = {"ROLE_STUDENT"})
    void shouldAllowUserToUpdateOwnDetails() throws Exception {
        User user = createAndSaveStudent("owner", "owner@email.com", "password");

        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setFullName("Updated Name");
        updateDto.setEmail("updated@email.com");

        mockMvc.perform(put("/api/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getFullName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@email.com");
    }

    @Test
    @WithMockUser(username = "other_user", authorities = {"ROLE_STUDENT"})
    void shouldForbidUserFromUpdatingAnotherUserDetails() throws Exception {
        User owner = createAndSaveStudent("owner", "owner@email.com", "password");

        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setFullName("Updated Name");

        mockMvc.perform(put("/api/users/" + owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "owner", authorities = {"ROLE_STUDENT"})
    void shouldAllowUserToDeleteOwnAccount() throws Exception {
        User user = createAndSaveStudent("owner", "owner@email.com", "password");

        mockMvc.perform(delete("/api/users/" + user.getId()))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(user.getId())).isEmpty();
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void shouldAllowAdminToDeleteAnotherUserAccount() throws Exception {
        User user = createAndSaveStudent("someuser", "someuser@email.com", "password");

        mockMvc.perform(delete("/api/users/" + user.getId()))
                .andExpect(status().isNoContent());
    }
}