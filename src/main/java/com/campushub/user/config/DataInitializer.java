package com.campushub.user.config;

import com.campushub.user.model.*;
import com.campushub.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String password = passwordEncoder.encode("test");
            String department = "Informatique";

            // ADMIN
            if (userRepository.findByEmail("admin@campushub.com").isEmpty()) {
                Admin admin = new Admin();
                admin.setUsername("admin");
                admin.setPassword(password);
                admin.setFullName("Administrateur Informatique");
                admin.setEmail("admin@campushub.com");
                admin.setRole(Role.ADMIN);
                admin.setDepartment(department);
                userRepository.save(admin);
                System.out.println("Seeder: Admin created");
            }

            // DEAN
            if (userRepository.findByEmail("dean@campushub.com").isEmpty()) {
                Dean dean = new Dean();
                dean.setUsername("dean");
                dean.setPassword(password);
                dean.setFullName("Doyen Informatique");
                dean.setEmail("dean@campushub.com");
                dean.setRole(Role.DEAN);
                dean.setDepartment(department);
                dean.setOfficeNumber("D-101");
                dean.setGrade("Professeur");
                userRepository.save(dean);
                System.out.println("Seeder: Dean created");
            }

            // TEACHER
            if (userRepository.findByEmail("teacher@campushub.com").isEmpty()) {
                Teacher teacher = new Teacher();
                teacher.setUsername("teacher");
                teacher.setPassword(password);
                teacher.setFullName("Enseignant Informatique");
                teacher.setEmail("teacher@campushub.com");
                teacher.setRole(Role.TEACHER);
                teacher.setDepartment(department);
                teacher.setOfficeNumber("T-202");
                teacher.setGrade("Maître de Conférences");
                userRepository.save(teacher);
                System.out.println("Seeder: Teacher created");
            }

            // STUDENT
            if (userRepository.findByEmail("student@campushub.com").isEmpty()) {
                Student student = new Student();
                student.setUsername("student");
                student.setPassword(password);
                student.setFullName("Étudiant Informatique");
                student.setEmail("student@campushub.com");
                student.setRole(Role.STUDENT);
                student.setDepartment(department);
                student.setStudentNumber("2024-STU-001");
                userRepository.save(student);
                System.out.println("Seeder: Student created");
            }
        };
    }
}
