package com.campushub.user.service;

import com.campushub.user.dto.UserCreationRequest;
import com.campushub.user.exception.UserAlreadyExistsException;
import com.campushub.user.model.*;
import com.campushub.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(UserCreationRequest request) {
        if (repo.existsByUsernameOrEmail(request.getUsername(), request.getEmail())) {
            throw new UserAlreadyExistsException("Username or email already taken.");
        }

        User newUser;

        switch (request.getRole()) {
            case STUDENT:
                Student student = new Student();
                student.setStudentNumber(request.getStudentNumber());
                newUser = student;
                break;
            case TEACHER:
                Teacher teacher = new Teacher();
                teacher.setOfficeNumber(request.getOfficeNumber());
                teacher.setGrade(request.getGrade());
                newUser = teacher;
                break;
            case DEAN:
                newUser = new Dean();
                break;
            case SECRETARIAT:
                newUser = new Secretariat();
                break;
            case ADMIN:
                newUser = new Admin();
                break;
            default:
                throw new IllegalArgumentException("Invalid role specified: " + request.getRole());
        }

        // Populate common fields
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword())); // Encode the password
        newUser.setFullName(request.getFullName());
        newUser.setEmail(request.getEmail());
        newUser.setDepartment(request.getDepartment());
        newUser.setRole(request.getRole());

        return repo.save(newUser);
    }

    @Override
    public User save(User user) {
        // This method is now primarily for updates.
        // We should check if the password is being updated and encode it if so.
        // For simplicity in this step, we assume the raw password is set on the user object for update
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
             user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return repo.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repo.findByUsername(username);
    }  

    @Override
    public Optional<User> findByEmail(String email) {
        return repo.findByEmail(email);
    }
                                                                        
                                                                           
    @Override
    public List<User> findAll() {
        return repo.findAll();
    }
    
    @Override
    public List<User> findAllByDepartment(String department) {
        return repo.findAllByDepartment(department);
    }
                                                                           
    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Override
    public String getUserRole(String email) {
        return repo.findByEmail(email)
                .map(user -> user.getRole().name())
                .orElse(null);
    }
}
