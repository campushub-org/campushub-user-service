package com.campushub.user.security;

import com.campushub.user.model.User;
import com.campushub.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserSecurity.class);

    public UserSecurity(UserService userService) {
        this.userService = userService;
    }

    public boolean isOwner(Authentication authentication, Long id) {
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("isOwner check failed: authentication is null or not authenticated.");
            return false;
        }

        String username = authentication.getName();
        log.info("isOwner check: authenticated username='{}' for requested id='{}'", username, id);

        User user = userService.findById(id).orElse(null);

        if (user == null) {
            log.warn("isOwner check failed: user with id='{}' not found.", id);
            return false;
        }

        boolean isOwner = user.getUsername().equals(username);
        log.info("isOwner check result for username='{}' and user.getUsername()='{}': {}", username, user.getUsername(), isOwner);

        return isOwner;
    }
}
