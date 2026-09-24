package com.OnlineStudentCourse.CourseRegistration.service;

import com.OnlineStudentCourse.CourseRegistration.dto.EnrollmentRequest;
import com.OnlineStudentCourse.CourseRegistration.exception.DuplicateResourceException;
import com.OnlineStudentCourse.CourseRegistration.exception.ResourceNotFoundException;
import com.OnlineStudentCourse.CourseRegistration.model.Course;
import com.OnlineStudentCourse.CourseRegistration.model.Enrollment;
import com.OnlineStudentCourse.CourseRegistration.model.Student;
import com.OnlineStudentCourse.CourseRegistration.repository.CourseRepository;
import com.OnlineStudentCourse.CourseRegistration.repository.EnrollmentRepository;
import com.OnlineStudentCourse.CourseRegistration.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                            StudentRepository studentRepository,
                            CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAllWithStudentAndCourse();
    }

    public List<Enrollment> getEnrollmentsForStudent(Long studentId) {
        return enrollmentRepository.findByStudentIdWithStudentAndCourse(studentId);
    }

    public Enrollment createEnrollment(EnrollmentRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.getStudentId()));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.getCourseId()));

        if (enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), course.getId())) {
            throw new DuplicateResourceException("Student is already enrolled in course '" + course.getCode() + "'");
        }

        validatePrerequisites(student.getId(), course);
        validateCapacity(course.getId());

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setSemester(request.getSemester().trim());
        enrollment.setStatus(request.getStatus() == null || request.getStatus().isBlank() ? "ENROLLED" : request.getStatus().trim().toUpperCase());
        return enrollmentRepository.save(enrollment);
    }

    public void deleteEnrollment(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));
        enrollmentRepository.delete(enrollment);
    }

    private void validatePrerequisites(Long studentId, Course course) {
        String prerequisites = course.getPrerequisites();
        if (prerequisites == null || prerequisites.isBlank()) {
            return;
        }

        List<String> requiredCodes = parsePrerequisites(prerequisites);
        for (String prerequisiteCode : requiredCodes) {
            boolean completed = enrollmentRepository.existsByStudentIdAndCourseCodeAndStatus(
                    studentId,
                    prerequisiteCode,
                    "COMPLETED"
            );

            if (!completed) {
                throw new IllegalArgumentException("Student must complete prerequisite course: " + prerequisiteCode);
            }
        }
    }

    private void validateCapacity(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        if (course.getCapacity() != null) {
            long enrolledCount = enrollmentRepository.countByCourseId(courseId);
            if (enrolledCount >= course.getCapacity()) {
                throw new IllegalArgumentException("Course '" + course.getCode() + "' has reached full capacity");
            }
        }
    }

    private List<String> parsePrerequisites(String prerequisites) {
        List<String> codes = new ArrayList<>();
        for (String item : prerequisites.split(",")) {
            String code = item.trim();
            if (!code.isEmpty()) {
                codes.add(code.toUpperCase());
            }
        }
        return codes;
    }
}
