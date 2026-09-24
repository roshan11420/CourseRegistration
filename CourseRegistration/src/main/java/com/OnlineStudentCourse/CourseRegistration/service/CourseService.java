package com.OnlineStudentCourse.CourseRegistration.service;

import com.OnlineStudentCourse.CourseRegistration.dto.CourseRequest;
import com.OnlineStudentCourse.CourseRegistration.exception.DuplicateResourceException;
import com.OnlineStudentCourse.CourseRegistration.exception.ResourceNotFoundException;
import com.OnlineStudentCourse.CourseRegistration.model.Course;
import com.OnlineStudentCourse.CourseRegistration.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    public Course createCourse(CourseRequest request) {
        String code = request.getCode().trim().toUpperCase();
        if (courseRepository.findByCode(code).isPresent()) {
            throw new DuplicateResourceException("Course code '" + code + "' already exists");
        }

        Course course = new Course();
        course.setCode(code);
        course.setTitle(request.getTitle().trim());
        course.setDepartment(request.getDepartment().trim());
        course.setCredits(request.getCredits());
        course.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");
        course.setPrerequisites(request.getPrerequisites() != null ? request.getPrerequisites().trim() : "");
        course.setSchedule(request.getSchedule().trim());
        course.setCapacity(request.getCapacity() != null ? request.getCapacity() : 30);
        return courseRepository.save(course);
    }

    public Course updateCourse(Long id, CourseRequest request) {
        Course course = getCourseById(id);
        String code = request.getCode().trim().toUpperCase();

        if (!course.getCode().equalsIgnoreCase(code)) {
            courseRepository.findByCode(code).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new DuplicateResourceException("Course code '" + code + "' already exists");
                }
            });
        }

        course.setCode(code);
        course.setTitle(request.getTitle().trim());
        course.setDepartment(request.getDepartment().trim());
        course.setCredits(request.getCredits());
        course.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");
        course.setPrerequisites(request.getPrerequisites() != null ? request.getPrerequisites().trim() : "");
        course.setSchedule(request.getSchedule().trim());
        course.setCapacity(request.getCapacity() != null ? request.getCapacity() : 30);
        return courseRepository.save(course);
    }

    public void deleteCourse(Long id) {
        Course course = getCourseById(id);
        courseRepository.delete(course);
    }
}
