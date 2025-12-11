package com.campushub.user.controller;

import com.campushub.user.dto.UserUpdateDto;
import com.campushub.user.model.User;
import com.campushub.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UserController.class);

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public List<User> all() {
        log.info("Entering all() method - GET /api/users");
        List<User> users = service.findAll();
        log.info("Exiting all() method. Found {} users", users.size());
        return users;
    }

    @GetMapping("/{id}")
    @PreAuthorize("@userSecurity.isOwner(authentication, #id) or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<User> get(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("@userSecurity.isOwner(authentication, #id) or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody UserUpdateDto updateDto) {
        return service.findById(id).map(existing -> {
            if (updateDto.getFullName() != null) {
                existing.setFullName(updateDto.getFullName());
            }
            if (updateDto.getEmail() != null) {
                existing.setEmail(updateDto.getEmail());
            }
            // Role update should only be allowed by ADMIN and needs specific handling
            // This method in UserUpdateDto does not expose role for regular users.
            // Admin role updates would be through a different DTO or specific endpoint.
            if (updateDto.getDepartment() != null) {
                existing.setDepartment(updateDto.getDepartment());
            }
            if (updateDto.getPassword() != null && !updateDto.getPassword().isEmpty()) {
                existing.setPassword(updateDto.getPassword()); // Password will be encoded by UserService.save()
            }
            if (updateDto.getProfilePictureUrl() != null) {
                existing.setProfilePictureUrl(updateDto.getProfilePictureUrl());
            }
            service.save(existing);
            return ResponseEntity.ok(existing);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@userSecurity.isOwner(authentication, #id) or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/role")
    public ResponseEntity<String> getRole() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return service.findByUsername(username)
                .map(user -> ResponseEntity.ok(user.getRole().name()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    @PreAuthorize("authentication.name == #username or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<User> getByUsername(@PathVariable String username) {
        return service.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/department/{department}")
    public List<User> getUsersByDepartment(@PathVariable String department) {
        return service.findAllByDepartment(department);
    }
}