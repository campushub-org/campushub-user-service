package com.campushub.user.service;

import com.campushub.user.dto.UserCreationRequest;
import com.campushub.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(UserCreationRequest request);
    User save(User user);
        Optional<User> findById(Long id);
        Optional<User> findByUsername(String username);
        Optional<User> findByEmail(String email);
        List<User> findAll();
        void deleteById(Long id);
        String getUserRole(String email);
    }
