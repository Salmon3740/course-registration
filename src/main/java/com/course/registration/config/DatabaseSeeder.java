package com.course.registration.config;

import com.course.registration.entity.Course;
import com.course.registration.entity.Student;
import com.course.registration.service.CourseService;
import com.course.registration.service.EnrollmentService;
import com.course.registration.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private StudentService studentService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Override
    public void run(String... args) throws Exception {
        // Only seed if database is empty
        if (studentService.getAllStudents().isEmpty() && courseService.getAllCourses().isEmpty()) {

            System.out.println("Seeding database with initial data...");

            // 1. Create 15 Sample Students
            Student s1 = studentService.saveStudent(new Student("Alice Smith", "alice@example.com", "pass123"));
            Student s2 = studentService.saveStudent(new Student("Bob Johnson", "bob@example.com", "pass123"));
            Student s3 = studentService.saveStudent(new Student("Charlie Brown", "charlie@example.com", "pass123"));
            Student s4 = studentService.saveStudent(new Student("Diana Prince", "diana@example.com", "pass123"));
            Student s5 = studentService.saveStudent(new Student("Evan Wright", "evan@example.com", "pass123"));
            Student s6 = studentService.saveStudent(new Student("Fiona Gallagher", "fiona@example.com", "pass123"));
            Student s7 = studentService.saveStudent(new Student("George Miller", "george@example.com", "pass123"));
            Student s8 = studentService.saveStudent(new Student("Hannah Abbott", "hannah@example.com", "pass123"));
            Student s9 = studentService.saveStudent(new Student("Ian Wright", "ian@example.com", "pass123"));
            Student s10 = studentService.saveStudent(new Student("Jane Doe", "jane@example.com", "pass123"));
            Student s11 = studentService.saveStudent(new Student("Kevin Hart", "kevin@example.com", "pass123"));
            studentService.saveStudent(new Student("Laura Palmer", "laura@example.com", "pass123"));
            studentService.saveStudent(new Student("Mike Ross", "mike@example.com", "pass123"));
            studentService.saveStudent(new Student("Nina Williams", "nina@example.com", "pass123"));
            studentService.saveStudent(new Student("Oscar Isaac", "oscar@example.com", "pass123"));

            // 2. Create 12 Sample Courses
            Course c1 = courseService.saveCourse(new Course("CS101: Intro to Computer Science", 40));
            Course c2 = courseService.saveCourse(new Course("ENG201: Modern Literature", 30));
            Course c3 = courseService.saveCourse(new Course("MTH301: Calculus III", 25));
            Course c4 = courseService.saveCourse(new Course("PHY101: Physics for Beginners", 35));
            Course c5 = courseService.saveCourse(new Course("CS305: Artificial Intelligence", 20));
            Course c6 = courseService.saveCourse(new Course("HIS102: World History", 40));
            Course c7 = courseService.saveCourse(new Course("BIO201: Cell Biology", 25));
            Course c8 = courseService.saveCourse(new Course("ECO101: Microeconomics", 50));
            Course c9 = courseService.saveCourse(new Course("ART101: Art History", 15));
            Course c10 = courseService.saveCourse(new Course("CHM101: General Chemistry", 30));
            Course c11 = courseService.saveCourse(new Course("SOC101: Sociology", 45));
            Course c12 = courseService.saveCourse(new Course("PSY101: Psychology", 40));

            // 3. Create Sample Enrollments
            try {
                enrollmentService.enrollStudent(s1.getId(), c1.getId());
                enrollmentService.enrollStudent(s1.getId(), c2.getId());
                enrollmentService.enrollStudent(s1.getId(), c5.getId());

                enrollmentService.enrollStudent(s2.getId(), c1.getId());
                enrollmentService.enrollStudent(s2.getId(), c3.getId());

                enrollmentService.enrollStudent(s3.getId(), c5.getId());
                enrollmentService.enrollStudent(s3.getId(), c8.getId());
                enrollmentService.enrollStudent(s3.getId(), c12.getId());

                enrollmentService.enrollStudent(s4.getId(), c2.getId());
                enrollmentService.enrollStudent(s4.getId(), c4.getId());
                enrollmentService.enrollStudent(s4.getId(), c6.getId());

                enrollmentService.enrollStudent(s5.getId(), c1.getId());
                enrollmentService.enrollStudent(s5.getId(), c5.getId());

                enrollmentService.enrollStudent(s6.getId(), c7.getId());
                enrollmentService.enrollStudent(s6.getId(), c10.getId());

                enrollmentService.enrollStudent(s7.getId(), c8.getId());
                enrollmentService.enrollStudent(s7.getId(), c11.getId());

                enrollmentService.enrollStudent(s8.getId(), c3.getId());
                enrollmentService.enrollStudent(s8.getId(), c12.getId());

                enrollmentService.enrollStudent(s9.getId(), c1.getId());
                enrollmentService.enrollStudent(s9.getId(), c9.getId());

                enrollmentService.enrollStudent(s10.getId(), c11.getId());
                enrollmentService.enrollStudent(s11.getId(), c12.getId());
            } catch (Exception e) {
                System.err.println(
                        "Warning: Pre-seeding some enrollments failed (likely JMS not ready): " + e.getMessage());
            }

            System.out.println("Database seeding completed successfully!");
        }
    }
}
