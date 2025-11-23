package com.campushub.user.repository;

import com.campushub.user.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    // You can add custom queries here, for example:
    // Optional<Teacher> findByOfficeNumber(String officeNumber);
}
