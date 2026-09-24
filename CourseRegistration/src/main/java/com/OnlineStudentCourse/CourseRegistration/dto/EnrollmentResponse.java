package com.OnlineStudentCourse.CourseRegistration.dto;

import java.time.LocalDateTime;

public class EnrollmentResponse {
    private Long id;
    private StudentSummary student;
    private CourseSummary course;
    private String semester;
    private String status;
    private LocalDateTime enrolledAt;

    public static class StudentSummary {
        public Long id;
        public String name;
        public String email;
    }

    public static class CourseSummary {
        public Long id;
        public String code;
        public String title;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public StudentSummary getStudent() { return student; }
    public void setStudent(StudentSummary student) { this.student = student; }
    public CourseSummary getCourse() { return course; }
    public void setCourse(CourseSummary course) { this.course = course; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(LocalDateTime enrolledAt) { this.enrolledAt = enrolledAt; }
}
