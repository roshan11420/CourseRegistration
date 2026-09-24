package com.OnlineStudentCourse.CourseRegistration.repository;

import com.OnlineStudentCourse.CourseRegistration.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    List<Enrollment> findByStudentId(Long studentId);

    @Query("SELECT e FROM Enrollment e JOIN FETCH e.student JOIN FETCH e.course WHERE e.student.id = :studentId")
    List<Enrollment> findByStudentIdWithStudentAndCourse(@Param("studentId") Long studentId);

    List<Enrollment> findByCourseId(Long courseId);

    long countByCourseId(Long courseId);

    @Query("SELECT COUNT(e) > 0 FROM Enrollment e JOIN e.course c WHERE e.student.id = :studentId AND c.code = :courseCode AND e.status = :status")
    boolean existsByStudentIdAndCourseCodeAndStatus(
            @Param("studentId") Long studentId,
            @Param("courseCode") String courseCode,
            @Param("status") String status
    );

    @Query("SELECT e FROM Enrollment e JOIN FETCH e.student JOIN FETCH e.course")
    List<Enrollment> findAllWithStudentAndCourse();
}
