package com.OnlineStudentCourse.CourseRegistration.repository;

import com.OnlineStudentCourse.CourseRegistration.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
    Optional<com.OnlineStudentCourse.CourseRegistration.model.Student> findByUsername(String username);
    boolean existsByUsername(String username);
}
