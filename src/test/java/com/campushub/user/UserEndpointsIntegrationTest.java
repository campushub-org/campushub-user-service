package com.campushub.user;

import com.campushub.user.dto.LoginRequest;
import com.campushub.user.dto.UserCreationRequest;
import com.campushub.user.model.Role;
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
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserEndpointsIntegrationTest {

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

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        UserCreationRequest request = new UserCreationRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setFullName("Test User");
        request.setEmail("test@email.com");
        request.setRole(Role.STUDENT);

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
        // Given a user exists
        UserCreationRequest registerRequest = new UserCreationRequest();
        registerRequest.setUsername("loginuser");
        registerRequest.setPassword("password");
        registerRequest.setFullName("Login User");
        registerRequest.setEmail("login@email.com");
        registerRequest.setRole(Role.STUDENT);
        User user = new User() {};
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(registerRequest.getRole());
        userRepository.save(user);


        // When logging in
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
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"ROLE_STUDENT"})
    void shouldAllowAuthenticatedUserToListAllUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }
    
    @Test
    void shouldForbidUnauthenticatedUserToListAllUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "owner", authorities = {"ROLE_STUDENT"})
    void shouldAllowUserToGetOwnDetails() throws Exception {
        User user = new User() {};
        user.setUsername("owner");
        user.setPassword("password");
        user.setRole(Role.STUDENT);
        user = userRepository.save(user);

        mockMvc.perform(get("/api/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("owner"));
    }
    
    @Test
    @WithMockUser(username = "other_user", authorities = {"ROLE_STUDENT"})
    void shouldForbidUserFromGettingAnotherUserDetails() throws Exception {
        User owner = new User() {};
        owner.setUsername("owner");
        owner.setPassword("password");
        owner.setRole(Role.STUDENT);
        owner = userRepository.save(owner);

        mockMvc.perform(get("/api/users/" + owner.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void shouldAllowAdminToGetAnotherUserDetails() throws Exception {
        User owner = new User() {};
        owner.setUsername("owner");
        owner.setPassword("password");
        owner.setRole(Role.STUDENT);
        owner = userRepository.save(owner);

        mockMvc.perform(get("/api/users/" + owner.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "owner", authorities = {"ROLE_STUDENT"})
    void shouldAllowUserToUpdateOwnDetails() throws Exception {
        User user = new User() {};
        user.setUsername("owner");
        user.setPassword("password");
        user.setRole(Role.STUDENT);
        user = userRepository.save(user);

        String updatedUserJson = "{\"fullName\": \"Updated Name\"}";

        mockMvc.perform(put("/api/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedUserJson))
                .andExpect(status().isOk());

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getFullName()).isEqualTo("Updated Name");
    }

    @Test
    @WithMockUser(username = "other_user", authorities = {"ROLE_STUDENT"})
    void shouldForbidUserFromUpdatingAnotherUserDetails() throws Exception {
        User owner = new User() {};
        owner.setUsername("owner");
        owner.setPassword("password");
        owner.setRole(Role.STUDENT);
        owner = userRepository.save(owner);

        String updatedUserJson = "{\"fullName\": \"Updated Name\"}";

        mockMvc.perform(put("/api/users/" + owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedUserJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "owner", authorities = {"ROLE_STUDENT"})
    void shouldAllowUserToDeleteOwnAccount() throws Exception {
        User user = new User() {};
        user.setUsername("owner");
        user.setPassword("password");
        user.setRole(Role.STUDENT);
        user = userRepository.save(user);

        mockMvc.perform(delete("/api/users/" + user.getId()))
                .andExpect(status().isNoContent());
        
        assertThat(userRepository.findById(user.getId())).isEmpty();
    }
    
    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void shouldAllowAdminToDeleteAnotherUserAccount() throws Exception {
        User user = new User() {};
        user.setUsername("user_to_delete");
        user.setPassword("password");
        user.setRole(Role.STUDENT);
        user = userRepository.save(user);

        mockMvc.perform(delete("/api/users/" + user.getId()))
                .andExpect(status().isNoContent());
        
        assertThat(userRepository.findById(user.getId())).isEmpty();
    }
}
