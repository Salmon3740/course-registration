package com.course.registration.controller;

import com.course.registration.entity.Enrollment;
import com.course.registration.entity.Student;
import com.course.registration.payload.EnrollmentRequest;
import com.course.registration.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @PostMapping("/enroll")
    public Enrollment enrollStudent(@RequestBody EnrollmentRequest request) {
        return enrollmentService.enrollStudent(request.getStudentId(), request.getCourseId());
    }

    @DeleteMapping("/drop")
    public String dropCourse(@RequestBody EnrollmentRequest request) {
        enrollmentService.dropCourse(request.getStudentId(), request.getCourseId());
        return "Course dropped successfully";
    }

    @GetMapping
    public List<Enrollment> getAllEnrollments() {
        return enrollmentService.getAllEnrollments();
    }

    @GetMapping("/course/{courseId}/students")
    public List<Student> getStudentsByCourse(@PathVariable Long courseId) {
        return enrollmentService.getStudentsByCourse(courseId);
    }
}