package com.OnlineStudentCourse.CourseRegistration.service;

import com.OnlineStudentCourse.CourseRegistration.dto.StudentRequest;
import com.OnlineStudentCourse.CourseRegistration.exception.DuplicateResourceException;
import com.OnlineStudentCourse.CourseRegistration.exception.ResourceNotFoundException;
import com.OnlineStudentCourse.CourseRegistration.model.Student;
import com.OnlineStudentCourse.CourseRegistration.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    public Student createStudent(StudentRequest request) {
        String email = request.getEmail().trim();
        if (studentRepository.findByEmail(email).isPresent()) {
            throw new DuplicateResourceException("A student with email '" + email + "' already exists");
        }

        Student student = new Student();
        student.setName(request.getName().trim());
        student.setEmail(email);
        student.setDepartment(request.getDepartment().trim());
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            String uname = request.getUsername().trim();
            if (studentRepository.existsByUsername(uname)) {
                throw new DuplicateResourceException("Username already exists for another student: " + uname);
            }
            student.setUsername(uname);
        }
        return studentRepository.save(student);
    }

    public Student attachUsername(Long studentId, String username) {
        Student student = getStudentById(studentId);
        if (studentRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Username already linked to another student: " + username);
        }
        student.setUsername(username);
        return studentRepository.save(student);
    }

    public Student updateStudent(Long id, StudentRequest request) {
        Student student = getStudentById(id);
        String updatedEmail = request.getEmail().trim();

        if (!student.getEmail().equalsIgnoreCase(updatedEmail)) {
            studentRepository.findByEmail(updatedEmail).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new DuplicateResourceException("A student with email '" + updatedEmail + "' already exists");
                }
            });
        }

        student.setName(request.getName().trim());
        student.setEmail(updatedEmail);
        student.setDepartment(request.getDepartment().trim());
        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        Student student = getStudentById(id);
        studentRepository.delete(student);
    }
}
