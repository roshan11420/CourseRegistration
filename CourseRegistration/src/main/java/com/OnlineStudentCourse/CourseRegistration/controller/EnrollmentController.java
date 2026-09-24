package com.OnlineStudentCourse.CourseRegistration.controller;

import com.OnlineStudentCourse.CourseRegistration.dto.EnrollmentRequest;
import com.OnlineStudentCourse.CourseRegistration.model.Enrollment;
import com.OnlineStudentCourse.CourseRegistration.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping("/enrollments")
    public List<com.OnlineStudentCourse.CourseRegistration.dto.EnrollmentResponse> getAllEnrollments() {
        return enrollmentService.getAllEnrollments().stream().map(this::toResponse).toList();
    }

    @GetMapping("/enrollments/student/{studentId}")
    public List<com.OnlineStudentCourse.CourseRegistration.dto.EnrollmentResponse> getEnrollmentsForStudent(@PathVariable Long studentId) {
        return enrollmentService.getEnrollmentsForStudent(studentId).stream().map(this::toResponse).toList();
    }

    @PostMapping("/enrollments")
    public ResponseEntity<com.OnlineStudentCourse.CourseRegistration.dto.EnrollmentResponse> createEnrollment(@Valid @RequestBody EnrollmentRequest request) {
        Enrollment saved = enrollmentService.createEnrollment(request);
        return ResponseEntity.created(URI.create("/api/enrollments/" + saved.getId())).body(toResponse(saved));
    }

    private com.OnlineStudentCourse.CourseRegistration.dto.EnrollmentResponse toResponse(Enrollment e) {
        com.OnlineStudentCourse.CourseRegistration.dto.EnrollmentResponse r = new com.OnlineStudentCourse.CourseRegistration.dto.EnrollmentResponse();
        r.setId(e.getId());
        com.OnlineStudentCourse.CourseRegistration.dto.EnrollmentResponse.StudentSummary s = new com.OnlineStudentCourse.CourseRegistration.dto.EnrollmentResponse.StudentSummary();
        if (e.getStudent() != null) {
            s.id = e.getStudent().getId();
            s.name = e.getStudent().getName();
            s.email = e.getStudent().getEmail();
        }
        r.setStudent(s);
        com.OnlineStudentCourse.CourseRegistration.dto.EnrollmentResponse.CourseSummary c = new com.OnlineStudentCourse.CourseRegistration.dto.EnrollmentResponse.CourseSummary();
        if (e.getCourse() != null) {
            c.id = e.getCourse().getId();
            c.code = e.getCourse().getCode();
            c.title = e.getCourse().getTitle();
        }
        r.setCourse(c);
        r.setSemester(e.getSemester());
        r.setStatus(e.getStatus());
        r.setEnrolledAt(e.getEnrolledAt());
        return r;
    }

    @DeleteMapping("/enrollments/{id}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable Long id) {
        enrollmentService.deleteEnrollment(id);
        return ResponseEntity.noContent().build();
    }
}
