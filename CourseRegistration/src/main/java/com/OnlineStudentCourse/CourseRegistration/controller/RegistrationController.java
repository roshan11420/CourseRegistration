package com.OnlineStudentCourse.CourseRegistration.controller;

import com.OnlineStudentCourse.CourseRegistration.dto.RegistrationRequest;
import com.OnlineStudentCourse.CourseRegistration.dto.StudentRequest;
import com.OnlineStudentCourse.CourseRegistration.model.Student;
import com.OnlineStudentCourse.CourseRegistration.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RegistrationController {

    private final com.OnlineStudentCourse.CourseRegistration.repository.UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StudentService studentService;

    public RegistrationController(com.OnlineStudentCourse.CourseRegistration.repository.UserRepository userRepository, PasswordEncoder passwordEncoder, StudentService studentService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.studentService = studentService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerStudent(@Valid @RequestBody RegistrationRequest request) {
        String username = request.getUsername().trim();
        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Username already exists"));
        }

        com.OnlineStudentCourse.CourseRegistration.model.AppUser user = new com.OnlineStudentCourse.CourseRegistration.model.AppUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("STUDENT");
        userRepository.save(user);

        // create student profile and attach username
        StudentRequest sreq = new StudentRequest();
        sreq.setName(request.getName());
        sreq.setEmail(request.getEmail());
        sreq.setDepartment(request.getDepartment());

        Student saved = studentService.createStudent(sreq);
        // attach username to student profile
        studentService.attachUsername(saved.getId(), username);

        return ResponseEntity.created(URI.create("/api/students/" + saved.getId())).body(java.util.Map.of("username", username));
    }
}
