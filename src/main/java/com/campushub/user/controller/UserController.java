package com.campushub.user.controller;

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

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<User> all() {
        return service.findAll();
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
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User user) {
        return service.findById(id).map(existing -> {
            // simple field updates
            existing.setUsername(user.getUsername());
            existing.setFullName(user.getFullName());
            existing.setEmail(user.getEmail());
            // Prevent users from changing their own role
            if (user.getRole() != null && SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                existing.setRole(user.getRole());
            }
            existing.setDepartment(user.getDepartment());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existing.setPassword(user.getPassword());
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
}
