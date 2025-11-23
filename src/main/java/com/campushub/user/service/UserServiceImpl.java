package com.campushub.user.service;

import com.campushub.user.dto.UserCreationRequest;
import com.campushub.user.model.*;
import com.campushub.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repo;

    public UserServiceImpl(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public User createUser(UserCreationRequest request) {
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
        newUser.setPassword(request.getPassword()); // Remember to encode the password in a real application
        newUser.setFullName(request.getFullName());
        newUser.setEmail(request.getEmail());
        newUser.setDepartment(request.getDepartment());
        newUser.setRole(request.getRole());

        return repo.save(newUser);
    }

    @Override
    public User save(User user) {
        // This method is now primarily for updates.
        // The createUser method should be used for creating new users with specific roles.
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
    public List<User> findAll() {
        return repo.findAll();
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}
