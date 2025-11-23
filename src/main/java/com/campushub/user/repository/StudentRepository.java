package com.campushub.user.repository;

import com.campushub.user.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    // You can add custom queries here, for example:
    // Optional<Student> findByStudentNumber(String studentNumber);
}
