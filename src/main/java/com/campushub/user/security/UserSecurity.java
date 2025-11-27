package com.campushub.user.security;

import com.campushub.user.model.User;
import com.campushub.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

    private final UserService userService;

    public UserSecurity(UserService userService) {
        this.userService = userService;
    }

    public boolean isOwner(Authentication authentication, Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        String username = authentication.getName();
        User user = userService.findById(id).orElse(null);
        return user != null && user.getUsername().equals(username);
    }
}
