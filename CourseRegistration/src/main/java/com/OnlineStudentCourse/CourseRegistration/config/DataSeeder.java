package com.OnlineStudentCourse.CourseRegistration.config;

import com.OnlineStudentCourse.CourseRegistration.model.Course;
import com.OnlineStudentCourse.CourseRegistration.model.Student;
import com.OnlineStudentCourse.CourseRegistration.repository.CourseRepository;
import com.OnlineStudentCourse.CourseRegistration.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final com.OnlineStudentCourse.CourseRegistration.repository.UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public DataSeeder(CourseRepository courseRepository, StudentRepository studentRepository, com.OnlineStudentCourse.CourseRegistration.repository.UserRepository userRepository, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (courseRepository.count() == 0) {
            courseRepository.saveAll(java.util.List.of(
                    buildCourse("CS101", "Introduction to Programming", "Computer Science", 3, "Mon/Wed 10:00-11:30", "Learn programming fundamentals and problem solving.", ""),
                    buildCourse("CS201", "Data Structures", "Computer Science", 3, "Tue/Thu 09:00-10:30", "Explore arrays, stacks, queues, trees, and graphs.", "CS101"),
                    buildCourse("BIO201", "Cell Biology", "Biology", 3, "Tue/Thu 09:00-10:30", "Covers cell structure, division, and core biological systems.", ""),
                    buildCourse("MATH201", "Discrete Mathematics", "Mathematics", 3, "Mon/Wed 13:00-14:30", "Covers logic, sets, combinatorics, and proofs.", ""),
                    buildCourse("ENG205", "Professional Communication", "English", 2, "Fri 08:00-10:00", "Develop professional writing and presentation skills.", ""),
                    buildCourse("BIO301", "Molecular Biology", "Biology", 4, "Tue/Thu 11:00-12:30", "Study cell processes and molecular systems.", "BIO201"),
                    buildCourse("CS301", "Advanced Java", "Computer Science", 4, "Mon/Wed/Fri 14:00-15:00", "Build enterprise Java applications and patterns.", "CS201")
            ));
        }

        if (studentRepository.count() == 0) {
            Student student = new Student();
            student.setName("Jessica Lee");
            student.setEmail("jessica@student.com");
            student.setDepartment("Computer Science");
            student.setUsername("jessica");
            studentRepository.save(student);

            // create corresponding app user
            if (!userRepository.existsByUsername("jessica")) {
                com.OnlineStudentCourse.CourseRegistration.model.AppUser u = new com.OnlineStudentCourse.CourseRegistration.model.AppUser();
                u.setUsername("jessica");
                u.setPassword(passwordEncoder.encode("student123"));
                u.setRole("STUDENT");
                userRepository.save(u);
            }
        }

        // ensure admin user exists
        if (!userRepository.existsByUsername("admin")) {
            com.OnlineStudentCourse.CourseRegistration.model.AppUser admin = new com.OnlineStudentCourse.CourseRegistration.model.AppUser();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            userRepository.save(admin);
        }
    }

    private Course buildCourse(String code, String title, String department, int credits, String schedule,
                              String description, String prerequisites) {
        Course course = new Course();
        course.setCode(code);
        course.setTitle(title);
        course.setDepartment(department);
        course.setCredits(credits);
        course.setSchedule(schedule);
        course.setDescription(description);
        course.setPrerequisites(prerequisites);
        course.setCapacity(30);
        return course;
    }
}
