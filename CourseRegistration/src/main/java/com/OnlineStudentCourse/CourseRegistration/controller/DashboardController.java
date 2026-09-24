package com.OnlineStudentCourse.CourseRegistration.controller;

import com.OnlineStudentCourse.CourseRegistration.repository.CourseRepository;
import com.OnlineStudentCourse.CourseRegistration.repository.EnrollmentRepository;
import com.OnlineStudentCourse.CourseRegistration.repository.StudentRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public DashboardController(StudentRepository studentRepository,
                              CourseRepository courseRepository,
                              EnrollmentRepository enrollmentRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard() {
        Map<String, Object> response = new HashMap<>();
        response.put("students", studentRepository.count());
        response.put("courses", courseRepository.count());
        response.put("enrollments", enrollmentRepository.count());
        response.put("availableSeats", courseRepository.findAll().stream()
                .mapToLong(course -> course.getCapacity() == null ? 0 : Math.max(0, course.getCapacity() - enrollmentRepository.countByCourseId(course.getId())))
                .sum());
        return response;
    }
}
