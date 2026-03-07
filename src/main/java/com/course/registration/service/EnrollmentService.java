package com.course.registration.service;

import com.course.registration.entity.Course;
import com.course.registration.entity.Enrollment;
import com.course.registration.entity.Student;
import com.course.registration.repository.CourseRepository;
import com.course.registration.repository.EnrollmentRepository;
import com.course.registration.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public Enrollment enrollStudent(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new RuntimeException("Student already enrolled in this course");
        }

        if (course.getAvailableSeats() <= 0) {
            throw new RuntimeException("No available seats for this course");
        }

        course.setAvailableSeats(course.getAvailableSeats() - 1);
        courseRepository.save(course);

        Enrollment enrollment = new Enrollment(student, course, LocalDateTime.now());
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        // Send JMS notification
        notificationService.sendEnrollmentNotification(student.getEmail(), student.getName(), course.getCourseName());

        return savedEnrollment;
    }

    @Transactional
    public void dropCourse(Long studentId, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        Course course = enrollment.getCourse();
        course.setAvailableSeats(course.getAvailableSeats() + 1);
        courseRepository.save(course);

        enrollmentRepository.delete(enrollment);
    }

    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    public List<Student> getStudentsByCourse(Long courseId) {
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);
        return enrollments.stream().map(Enrollment::getStudent).collect(Collectors.toList());
    }
}