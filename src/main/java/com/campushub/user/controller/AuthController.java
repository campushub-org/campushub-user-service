package com.campushub.user.controller;

import com.campushub.user.dto.LoginRequest;
import com.campushub.user.dto.LoginResponse;
import com.campushub.user.dto.UserCreationRequest;
import com.campushub.user.model.User;
import com.campushub.user.security.JwtService;
import com.campushub.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userService.findByUsername(loginRequest.getUsername()).orElseThrow();
        String jwt = jwtService.generateTokenForUser(user);

        return ResponseEntity.ok(new LoginResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserCreationRequest request) {
        // Here we assume the role will be part of the request or defaulted (e.g., STUDENT)
        // For a public registration, you might want to enforce a default role like STUDENT
        // and not allow arbitrary role selection via the request for security reasons.
        // For simplicity, we're using the provided request directly for now.
        User newUser = userService.createUser(request);
        return ResponseEntity.created(URI.create("/api/users/" + newUser.getId())).body(newUser);
    }
}
