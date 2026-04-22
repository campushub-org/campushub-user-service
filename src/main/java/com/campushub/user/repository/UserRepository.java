package com.campushub.user.repository;

import com.campushub.user.model.Role;
import com.campushub.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsernameOrEmail(String username, String email);
    List<User> findAllByDepartment(String department);
    List<User> findByDepartmentAndRole(String department, Role role);

    @Query("SELECT u FROM User u")
    List<User> findAll();
}

