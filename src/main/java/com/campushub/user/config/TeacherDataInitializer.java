package com.campushub.user.config;

import com.campushub.user.model.Role;
import com.campushub.user.model.Teacher;
import com.campushub.user.repository.TeacherRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Configuration
public class TeacherDataInitializer {

    private static final Logger log = LoggerFactory.getLogger(TeacherDataInitializer.class);

    @Bean
    CommandLineRunner initTeachers(TeacherRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (repository.count() < 10) {
                log.info("Chargement des enseignants depuis teachers.json...");
                ObjectMapper mapper = new ObjectMapper();
                try {
                    InputStream inputStream = new ClassPathResource("/teachers.json").getInputStream();
                    List<Map<String, String>> teachersData = mapper.readValue(inputStream, new TypeReference<List<Map<String, String>>>() {});
                    
                    for (Map<String, String> data : teachersData) {
                        String email = data.get("email");
                        if (!repository.existsByEmail(email)) {
                            Teacher teacher = new Teacher();
                            teacher.setUsername(data.get("username"));
                            teacher.setFullName(data.get("fullName"));
                            teacher.setEmail(email);
                            teacher.setPassword(passwordEncoder.encode(data.get("password")));
                            teacher.setRole(Role.TEACHER);
                            teacher.setDepartment(data.get("department"));
                            
                            repository.save(teacher);
                            log.info("Enseignant importé : {}", teacher.getFullName());
                        }
                    }
                    log.info("Importation des enseignants terminée !");
                } catch (Exception e) {
                    log.error("Erreur lors de l'importation des enseignants: {}", e.getMessage());
                }
            } else {
                log.info("Les enseignants sont déjà présents en base de données.");
            }
        };
    }
}
